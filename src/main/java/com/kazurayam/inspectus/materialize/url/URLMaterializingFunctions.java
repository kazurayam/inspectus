package com.kazurayam.inspectus.materialize.url;

import com.kazurayam.inspectus.materialize.discovery.Target;
import com.kazurayam.materialstore.core.FileType;
import com.kazurayam.materialstore.core.FileTypeUtil;
import com.kazurayam.materialstore.core.JobName;
import com.kazurayam.materialstore.core.JobTimestamp;
import com.kazurayam.materialstore.core.Material;
import com.kazurayam.materialstore.core.MaterialstoreException;
import com.kazurayam.materialstore.core.Metadata;
import com.kazurayam.materialstore.core.Store;
import org.apache.hc.client5.http.ClientProtocolException;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Objects;

public class URLMaterializingFunctions {

    private Store store;
    private JobName jobName;
    private JobTimestamp jobTimestamp;

    public URLMaterializingFunctions(Store store, JobName jobName, JobTimestamp jobTimestamp) {
        this.store = store;
        this.jobName = jobName;
        this.jobTimestamp = jobTimestamp;
    }

    private static final Logger logger =
            LoggerFactory.getLogger(URLMaterializingFunctions.class);

    /**
     * get the URL, store the content into the store.
     *
     * Use Apache HTTPComponents HttpClient
     * see the original code at
     * https://github.com/apache/httpcomponents-client/blob/5.1.x/httpclient5/src/test/java/org/apache/hc/client5/http/examples/ClientWithResponseHandler.java
     */
    public URLMaterializingFunction<Target, Material>
            storeURL = (target) -> {
        Objects.requireNonNull(target);
        try (final CloseableHttpClient httpclient = HttpClients.createDefault()) {
            final HttpGet httpget = new HttpGet(target.getUrl().toString());
            logger.debug("[storeHttpResource] " + "Executing request " +
                    httpget.getMethod() + " " + httpget.getUri());
            // Create a custom response handler
            final HttpClientResponseHandler<DigestedResponse> responseHandler =
                    response -> {
                        final int status = response.getCode();
                        if (status >= HttpStatus.SC_SUCCESS && status < HttpStatus.SC_REDIRECTION) {
                            final HttpEntity entity = response.getEntity();
                            if (entity != null) {
                                DigestedResponse digested = new DigestedResponse(EntityUtils.toByteArray(entity));
                                Header contentType = response.getHeader("Content-Type");
                                if (contentType != null) {
                                    digested.setContentType(contentType);
                                }
                                return digested;
                            } else {
                                return null;
                            }
                        } else {
                            throw new ClientProtocolException("Unexpected response status: " + status);
                        }
                    };
            final DigestedResponse myResponse = httpclient.execute(httpget, responseHandler);
            FileType fileType = FileTypeUtil.ofMimeType(myResponse.getMediaType());
            Metadata metadata = Metadata.builder(target.getUrl())
                    .putAll(target.getAttributes()).build();
            byte[] bytes = myResponse.getContent();
            return this.store.write(this.jobName, this.jobTimestamp, fileType, metadata, bytes);
        } catch (IOException | URISyntaxException e) {
            throw new MaterialstoreException(e);
        }
    };

}
