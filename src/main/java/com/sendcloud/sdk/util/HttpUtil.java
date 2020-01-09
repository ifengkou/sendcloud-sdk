package com.sendcloud.sdk.util;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;

import javax.net.ssl.SSLContext;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;

import net.sf.json.JSONObject;
import net.sf.json.util.JSONUtils;

public class HttpUtil {

	private static PoolingHttpClientConnectionManager connManager = null;
	private static CloseableHttpClient httpClient = null;
	static {
		SSLContext sslcontext = null;
		try {
			sslcontext = SSLContexts.custom().loadTrustMaterial(new TrustStrategy() {
				public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
					return true;
				}
			}).build();
		} catch (KeyManagementException | NoSuchAlgorithmException | KeyStoreException e) {
			e.printStackTrace();
		}
		SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(sslcontext,
				new String[] { "TLSv1", "TLSv1.1", "TLSv1.2" }, null,
				SSLConnectionSocketFactory.getDefaultHostnameVerifier());
		Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory> create()
				.register("https", sslConnectionSocketFactory).register("http", new PlainConnectionSocketFactory())
				.build();
		connManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
		connManager.setMaxTotal(1000);
		connManager.setDefaultMaxPerRoute(100);
		connManager.setValidateAfterInactivity(2000);
		int connection_timeout = 10000;
		int so_timeout = 10000;
		RequestConfig config = RequestConfig.custom().setSocketTimeout(so_timeout)
				.setConnectionRequestTimeout(connection_timeout).setConnectTimeout(connection_timeout)
				.setCookieSpec(CookieSpecs.IGNORE_COOKIES).build();
		httpClient = HttpClients.custom().setConnectionManager(connManager).setDefaultRequestConfig(config)
				.setDefaultHeaders(Arrays.asList(
						new BasicHeader("User-Agent",
								"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.110 Safari/537.36"),
						new BasicHeader("charset", "utf-8"), new BasicHeader("Connection", "close")))
				.build();
	}

	public static ResponseData post(HttpPost httpPost) throws ClientProtocolException, IOException {
		CloseableHttpResponse response = null;
		try {
			response = httpClient.execute(httpPost);
			return validate(response);
		} catch (Exception e) {
			e.printStackTrace();
			return validate(response);
		} finally {
			if (response != null)
				response.close();
		}
	}

	public static void destroy() {
		try {
			System.out.println("destroy httpClient");
			httpClient.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static CloseableHttpClient getHttpClient() {
		return httpClient;
	}

	private static ResponseData validate(HttpResponse response) throws ParseException, IOException {
		ResponseData result = new ResponseData();
		if (response != null && response.getEntity() != null) {
			String s = EntityUtils.toString(response.getEntity());
			if (JSONUtils.mayBeJSON(s)) {
				JSONObject json = JSONObject.fromObject(s);
				if (json.containsKey("statusCode")) {
					result.setStatusCode(json.getInt("statusCode"));
					result.setMessage(json.getString("message"));
					result.setResult(json.getBoolean("result"));
					result.setInfo(json.getJSONObject("info").toString());
				} else {
					result.setStatusCode(500);
					result.setMessage(json.toString());
				}
			} else {
				result.setStatusCode(response.getStatusLine().getStatusCode());
				result.setMessage("发送失败");
				result.setResult(false);
			}
		} else {
			result.setStatusCode(500);
			result.setMessage("发送失败");
			result.setResult(false);
		}
		return result;
	}
}