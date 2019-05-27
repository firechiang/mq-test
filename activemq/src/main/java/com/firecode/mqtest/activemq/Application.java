package com.firecode.mqtest.activemq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {
	
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
	
	//protected final RestTemplate restTemplate;
	
/*	public Application() {
		restTemplate = new RestTemplateBuilder().setConnectTimeout(30000).build();
		MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
		converter.setDefaultCharset(StandardCharsets.UTF_8);
		converter.setSupportedMediaTypes(Arrays.asList(MediaType.TEXT_PLAIN,MediaType.APPLICATION_JSON_UTF8,MediaType.IMAGE_JPEG));
		//restTemplate.setMessageConverters(Arrays.asList(converter));
	}*/
	
	
/*	public RestTemplate get(){
		
		return restTemplate;
	}*/
	
	
	
/*	@SuppressWarnings("resource")
	public static void main(String[] args) throws IOException {
		String url = "https://api.weixin.qq.com/wxa/getwxacode?access_token=13_caa0pZd_tz958rG4olrjkmsG_7jN688zUBsyU3FvMCzmhtie-MAiQWE2ZCt1nHTCHqV7S5v8u6-mlBl85BVQDmd-XDCdX0j1MuTH7q2ncQ08RaikM8In1GQg0unm1K0APdpmFlNkkevwR9S-CBOiAGALKX";
		Application a = new Application();
		RestTemplate restTemplate = a.get();
		
		Map<String,Object> param = new HashMap<>();
		param.put("path", "pages/projectDetail/main?projectid=1031732082727391233");
		Map<String,Object> map = new HashMap<>();
		map.put("r", 255);
		map.put("g", 255);
		map.put("b", 255);
		param.put("line_color", map);
		

		
		HttpEntity<Map<String,Object>> request = new HttpEntity<>(param);
		//ResponseEntity<byte[]> response = restTemplate.postForEntity(url, request, byte[].class);
		byte[] response = restTemplate.postForObject(url, request, byte[].class);
		System.err.println(response.length);
		FileOutputStream fileOutputStream = new FileOutputStream(new File("E:\\testtest\\ttttt.jpg"));
		fileOutputStream.write(response);
		//SpringApplication.run(Application.class, args);
	}*/
	
	
	protected static class WeChatResponse {
		
		private String access_token;
		
		private Long expires_in;
		
		private String errcode;
		
		private String errmsg;

		public String getAccess_token() {
			return access_token;
		}

		public void setAccess_token(String access_token) {
			this.access_token = access_token;
		}

		public Long getExpires_in() {
			return expires_in;
		}

		public void setExpires_in(Long expires_in) {
			this.expires_in = expires_in;
		}

		public String getErrcode() {
			return errcode;
		}

		public void setErrcode(String errcode) {
			this.errcode = errcode;
		}

		public String getErrmsg() {
			return errmsg;
		}

		public void setErrmsg(String errmsg) {
			this.errmsg = errmsg;
		}
		
	}

}
