package org.oxerr.okcoin.rest.service.web;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.message.BasicHeader;
import org.oxerr.okcoin.rest.dto.valuereader.JsonValueReader;
import org.oxerr.okcoin.rest.dto.valuereader.JsonValueTypeRefReader;
import org.oxerr.okcoin.rest.dto.valuereader.ValueReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class HttpClient implements AutoCloseable {

	private final Logger log = LoggerFactory.getLogger(HttpClient.class);

	private final CloseableHttpClient httpClient;

	private final ObjectMapper objectMapper;

	public HttpClient(CloseableHttpClient httpClient, ObjectMapper objectMapper) {
		this.httpClient = httpClient;
		this.objectMapper = objectMapper;
	}

	public HttpClient(
			int socketTimeout,
			int connectTimeout,
			int connectionRequestTimeout) {
		HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
		httpClientBuilder.setRedirectStrategy(new LaxRedirectStrategy());

		String userAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_8_5) AppleWebKit/537.71 (KHTML, like Gecko) Version/6.1 Safari/537.71";
		httpClientBuilder.setUserAgent(userAgent);

		Collection<Header> defaultHeaders = new ArrayList<>();
		defaultHeaders.add(new BasicHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8"));
		httpClientBuilder.setDefaultHeaders(defaultHeaders);

		RequestConfig defaultRequestConfig = RequestConfig.custom()
				.setSocketTimeout(socketTimeout)
				.setConnectTimeout(connectTimeout)
				.setConnectionRequestTimeout(connectionRequestTimeout)
				.build();
		httpClientBuilder.setDefaultRequestConfig(defaultRequestConfig);

		httpClient = httpClientBuilder.build();

		objectMapper = new ObjectMapper();
		objectMapper.configure(Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
	}

	public <T> T get(URI uri, Class<T> valueType) throws IOException {
		return get(uri, new JsonValueReader<>(objectMapper, valueType));
	}

	public <T> T get(URI uri, TypeReference<T> valueTypeRef) throws IOException {
		return get(uri, new JsonValueTypeRefReader<>(objectMapper, valueTypeRef));
	}

	public <T> T get(URI uri, ValueReader<T> valueReader) throws IOException {
		return execute(new HttpGet(uri), valueReader);
	}

	public <T> T post(URI uri, ValueReader<T> valueReader,
			NameValuePair... params) throws IOException {
		return post(uri, valueReader, Arrays.asList(params));
	}

	public <T> T post(URI uri, ValueReader<T> valueReader,
			List<NameValuePair> params) throws IOException {
		HttpPost post = new HttpPost(uri);
		post.setEntity(new UrlEncodedFormEntity(params));
		return execute(post, valueReader);
	}

	public <T> T post(URI uri, ValueReader<T> valueReader, String content,
			String charset) throws IOException {
		HttpPost post = new HttpPost(uri);
		post.setEntity(new StringEntity(content, charset));
		return execute(post, valueReader);
	}

	public <T> T post(URI uri, ValueReader<T> valueReader, String content,
			ContentType contentType) throws IOException {
		HttpPost post = new HttpPost(uri);
		post.setEntity(new StringEntity(content, contentType));
		return execute(post, valueReader);
	}

	@Deprecated
	public <T> T execute(
			final ValueReader<T> valueReader,
			final HttpUriRequest request) throws IOException {
		return execute(request, valueReader);
	}

	public <T> T execute(HttpUriRequest request, ValueReader<T> valueReader) throws IOException {
		log.debug("Executing: {}", request.getURI());
		try (CloseableHttpResponse response = httpClient.execute(request)) {
			return valueReader.read(response);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void close() throws IOException {
		httpClient.close();
	}

}
