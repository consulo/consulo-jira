/*
 * Copyright 2013-2026 consulo.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package consulo.jira;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonParser;
import consulo.accountManagement.AccountConnection;
import consulo.accountManagement.AccountRequestDecorator;
import consulo.http.client.HttpApiHelper;
import consulo.logging.Logger;
import consulo.util.lang.StringUtil;
import org.jspecify.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.net.URLEncoder;
import java.net.http.HttpRequest;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * The REST API version 2 of one Jira server, signed in with an account connection.
 *
 * @author VISTALL
 * @since 2026-11-09
 */
public final class JiraClient {
    private static final Logger LOG = Logger.getInstance(JiraClient.class);
    private static final HttpApiHelper HTTP = HttpApiHelper.create(LOG);
    private static final Gson GSON = new GsonBuilder().serializeNulls().create();

    private final AccountConnection myConnection;
    private final String myServerUrl;

    private JiraClient(AccountConnection connection, String serverUrl) {
        myConnection = connection;
        myServerUrl = serverUrl;
    }

    public static JiraClient create(AccountConnection connection) {
        String serverUrl = connection.getServerUrl();
        if (StringUtil.isEmpty(serverUrl)) {
            throw new IllegalStateException("Jira server URL is not set");
        }
        return new JiraClient(connection, trimServerUrl(serverUrl));
    }

    public static String trimServerUrl(String serverUrl) {
        return StringUtil.trimEnd(serverUrl.trim(), "/");
    }

    public static String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    public String getServerUrl() {
        return myServerUrl;
    }

    public CompletableFuture<JsonElement> get(String path) {
        return send("GET", path, null);
    }

    public CompletableFuture<JsonElement> post(String path, Object body) {
        return send("POST", path, body);
    }

    public CompletableFuture<JsonElement> put(String path, Object body) {
        return send("PUT", path, body);
    }

    public CompletableFuture<JsonElement> delete(String path) {
        return send("DELETE", path, null);
    }

    public CompletableFuture<JsonElement> send(String method, String path, @Nullable Object body) {
        HttpRequest.BodyPublisher publisher = body == null
            ? HttpRequest.BodyPublishers.noBody()
            : HttpRequest.BodyPublishers.ofString(GSON.toJson(body), StandardCharsets.UTF_8);

        return request(path).thenCompose(builder -> {
            builder.method(method, publisher);
            if (body != null) {
                builder.header("Content-Type", "application/json");
            }
            return HTTP.sendAndReadBody(builder.build(), JiraClient::readJson);
        });
    }

    public CompletableFuture<JsonElement> upload(String path, Path file) {
        String boundary = "----consulo" + UUID.randomUUID().toString().replace("-", "");
        byte[] content;
        try {
            content = Files.readAllBytes(file);
        }
        catch (IOException e) {
            return CompletableFuture.failedFuture(e);
        }

        String head = "--" + boundary + "\r\n"
            + "Content-Disposition: form-data; name=\"file\"; filename=\"" + file.getFileName() + "\"\r\n"
            + "Content-Type: application/octet-stream\r\n\r\n";
        String tail = "\r\n--" + boundary + "--\r\n";

        List<byte[]> parts = new ArrayList<>();
        parts.add(head.getBytes(StandardCharsets.UTF_8));
        parts.add(content);
        parts.add(tail.getBytes(StandardCharsets.UTF_8));

        return request(path).thenCompose(builder -> {
            builder.POST(HttpRequest.BodyPublishers.ofByteArrays(parts));
            builder.header("Content-Type", "multipart/form-data; boundary=" + boundary);
            builder.header("X-Atlassian-Token", "no-check");
            return HTTP.sendAndReadBody(builder.build(), JiraClient::readJson);
        });
    }

    private CompletableFuture<HttpRequest.Builder> request(String path) {
        Map<String, String> headers = new LinkedHashMap<>();
        Map<String, String> parameters = new LinkedHashMap<>();
        myConnection.decorate(new AccountRequestDecorator() {
            @Override
            public void header(String name, String value) {
                headers.put(name, value);
            }

            @Override
            public void queryParameter(String name, String value) {
                parameters.put(name, value);
            }
        });

        StringBuilder url = new StringBuilder(myServerUrl).append("/rest/api/2/").append(path);
        for (Map.Entry<String, String> parameter : parameters.entrySet()) {
            url.append(url.indexOf("?") < 0 ? '?' : '&').append(encode(parameter.getKey())).append('=').append(encode(parameter.getValue()));
        }

        return HTTP.request(url.toString()).thenApply(builder -> {
            builder.header("Accept", "application/json");
            headers.forEach(builder::header);
            return builder;
        });
    }

    private static JsonElement readJson(InputStream stream, @Nullable String mimeType, @Nullable Charset charset) throws IOException {
        byte[] bytes = stream.readAllBytes();
        if (bytes.length == 0) {
            return JsonNull.INSTANCE;
        }
        try (InputStreamReader reader = new InputStreamReader(new ByteArrayInputStream(bytes), charset == null ? StandardCharsets.UTF_8 : charset)) {
            return JsonParser.parseReader(reader);
        }
        catch (RuntimeException e) {
            throw new UncheckedIOException(new IOException("Not a JSON answer from Jira", e));
        }
    }
}
