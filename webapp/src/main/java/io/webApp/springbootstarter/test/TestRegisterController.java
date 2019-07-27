//package io.webApp.springbootstarter.test;
//
//import io.webApp.springbootstarter.register.register;
//
//
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//import org.junit.Before;
//import org.junit.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//import org.springframework.web.context.WebApplicationContext;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.databind.ObjectWriter;
//import com.fasterxml.jackson.databind.SerializationFeature;
//
//
//public class TestRegisterController extends SpringBootRegisterUserTest {
//
//	@Autowired
//	private WebApplicationContext webApplicationContext;
//
////	@Autowired
////	public register register;
//
//	
//	private MockMvc mockMvc;
//
//	
//	@Before
//	public void setup() {
//		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
//	}
//	
//	@Test
//	public void testregister() throws Exception {
//		
//		mockMvc.perform(get("/test")).andExpect(status().isOk())
//				.andExpect(content().contentType("application/json;charset=UTF-8"))
//				.andExpect(jsonPath("$.email").value("paavan@gmail.com")).andExpect(jsonPath("$.password").value("Pass@123")) .andDo(print());;
//				
//	}
//	
//
//	
//	@Test
//	public void teststore() throws Exception {
//		
//		register register = new register();
//		register.setID(1);
//		register.setEmail("paavan@gmail.com");
//		register.setPassword("Pass@123");
//		
//		 ObjectMapper mapper = new ObjectMapper();
//		 mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
//		 ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
//		 String requestJson=ow.writeValueAsString(register );
//		
//
//			        mockMvc.perform(post("/user/register")
//			        	       .contentType(MediaType.APPLICATION_JSON)
//			        	       .content(requestJson)
//			        	       .accept(MediaType.APPLICATION_JSON))
//			        	       .andExpect(status().isOk());
//			        
//
//
//	}
//
//	
//}
