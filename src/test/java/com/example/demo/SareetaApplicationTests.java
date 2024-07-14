package com.example.demo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.requests.CreateUserRequest;
import com.example.demo.model.requests.ModifyCartRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.IntStream;

import org.apache.catalina.security.SecurityConfig;

import jakarta.transaction.Transactional;

@Transactional
@SpringBootTest(classes = SareetaApplication.class)
@ContextConfiguration(classes = SecurityConfig.class)
@AutoConfigureMockMvc
public class SareetaApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext webApplicationContext;

	private ObjectMapper Obj = new ObjectMapper();

	private Random random = new Random(System.currentTimeMillis());

	// private String authToken;

	@Test
	public void contextLoads() {
	}

	@BeforeEach
	public void setUp() throws JsonProcessingException, Exception {
		mockMvc = MockMvcBuilders
				.webAppContextSetup(webApplicationContext)
				.apply(springSecurity())
				.build();
	}

	@Test
	public void jwtFailed() throws Exception {
		this.mockMvc.perform(get("/api/user/id/{id}", 1)).andDo(print())
				.andExpect(status().isForbidden());
	}

	@Test
	public void getUserByIdNotFound() throws Exception {
		this.mockMvc.perform(get("/api/user/id/{id}", 0)
				.with(SecurityMockMvcRequestPostProcessors.jwt())).andDo(print())
				.andExpect(status().isNotFound());
	}

	@Test
	public void getUserById() throws Exception {
		User user = getDefaultUser();

		user.setUsername(Integer.toString(random.nextInt(1000)));

		user = createUser(user);

		this.mockMvc.perform(get("/api/user/id/{id}", user.getId())
				.with(SecurityMockMvcRequestPostProcessors.jwt())).andDo(print())
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(content().json(Obj.writeValueAsString(user)));
	}

	@Test
	public void getUserInfoNotFound() throws Exception {
		this.mockMvc.perform(get("/api/user/{username}", "notExist")
				.with(SecurityMockMvcRequestPostProcessors.jwt())).andDo(print())
				.andExpect(status().isNotFound());
	}

	@Test
	public void getUserInfo() throws Exception {
		User user = getDefaultUser();

		user.setUsername(Integer.toString(random.nextInt(1000)));

		user = createUser(user);

		this.mockMvc.perform(get("/api/user/{username}", user.getUsername())
				.with(SecurityMockMvcRequestPostProcessors.jwt())).andDo(print())
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(content().json(Obj.writeValueAsString(user)));
	}

	@Test
	public void createUserFailedUsernameCheck() throws Exception {
		User user = getDefaultUser();

		user.setUsername(Integer.toString(random.nextInt(1000)));

		user = createUser(user);

		CreateUserRequest createUserRequest = new CreateUserRequest();
		createUserRequest.setUsername(user.getUsername());
		createUserRequest.setPassword("Test@12345");
		createUserRequest.setConfirmPassword(createUserRequest.getPassword());

		this.mockMvc.perform(post("/api/user/create")
				.contentType(MediaType.APPLICATION_JSON)
				.content(Obj.writeValueAsBytes(createUserRequest))).andDo(print())
				.andExpect(status().isBadRequest());
	}

	@Test
	public void createUserFailedPasswordCheck() throws Exception {
		CreateUserRequest createUserRequest = new CreateUserRequest();

		createUserRequest.setUsername("yeah");
		createUserRequest.setPassword("12345");
		createUserRequest.setConfirmPassword("12345");

		this.mockMvc.perform(post("/api/user/create")
				.contentType(MediaType.APPLICATION_JSON)
				.content(Obj.writeValueAsBytes(createUserRequest))).andDo(print())
				.andExpect(status().isBadRequest());
	}

	@Test
	public void createUserFailedPasswordConfirmCheck() throws Exception {
		CreateUserRequest createUserRequest = new CreateUserRequest();

		createUserRequest.setUsername("yeah");
		createUserRequest.setPassword("12345");
		createUserRequest.setConfirmPassword("diff");

		this.mockMvc.perform(post("/api/user/create")
				.contentType(MediaType.APPLICATION_JSON)
				.content(Obj.writeValueAsBytes(createUserRequest))).andDo(print())
				.andExpect(status().isBadRequest());
	}

	@Test
	public void createUserSuccess() throws Exception {
		User user = getDefaultUser();

		CreateUserRequest createUserRequest = new CreateUserRequest();

		createUserRequest.setUsername(user.getUsername());
		createUserRequest.setPassword(user.getPassword());
		createUserRequest.setConfirmPassword(createUserRequest.getPassword());

		user.setPassword(null);

		MvcResult result = this.mockMvc.perform(post("/api/user/create")
				.contentType(MediaType.APPLICATION_JSON)
				.content(Obj.writeValueAsBytes(createUserRequest))).andDo(print())
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andReturn();

		User userResult = Obj.readValue(result.getResponse().getContentAsString(), User.class);

		user.setId(userResult.getId());

		assertThat(user).usingRecursiveComparison().isEqualTo(userResult);
	}

	@Test
	public void getItemList() throws Exception {
		this.mockMvc.perform(get("/api/item")
				.with(SecurityMockMvcRequestPostProcessors.jwt())).andDo(print())
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(content().json(Obj.writeValueAsString(getDefaultItemList())));
	}

	@Test
	public void getItemNotFound() throws Exception {
		this.mockMvc.perform(get("/api/item/{id}", 0)
				.with(SecurityMockMvcRequestPostProcessors.jwt())).andDo(print())
				.andExpect(status().isNotFound());
	}

	@Test
	public void getItem() throws Exception {
		Long id = (long) 1;

		Optional<Item> defaultItem = getDefaultItemList().stream().filter(item -> item.getId() == id).findFirst();

		assertTrue(defaultItem.isPresent());

		this.mockMvc.perform(get("/api/item/{id}", id)
				.with(SecurityMockMvcRequestPostProcessors.jwt())).andDo(print())
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(content().json(Obj.writeValueAsString(defaultItem.get())));
	}

	@Test
	public void getItemByNameNotFound() throws Exception {
		this.mockMvc.perform(get("/api/item/name/{name}", "temp")
				.with(SecurityMockMvcRequestPostProcessors.jwt())).andDo(print())
				.andExpect(status().isNotFound());
	}

	@Test
	public void getItemByName() throws Exception {
		String name = "Round Widget";

		List<Item> defaultItemsList = getDefaultItemList().stream().filter(item -> item.getName().contains(name))
				.toList();

		this.mockMvc.perform(get("/api/item/name/{name}", name)
				.with(SecurityMockMvcRequestPostProcessors.jwt())).andDo(print())
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(content().json(Obj.writeValueAsString(defaultItemsList)));
	}

	@Test
	public void addItemToCartUserNotFound() throws Exception {
		ModifyCartRequest cartRequest = new ModifyCartRequest();

		cartRequest.setUsername("nowhere");
		cartRequest.setItemId(1);
		cartRequest.setQuantity(1);

		this.mockMvc.perform(post("/api/cart/addToCart")
				.contentType(MediaType.APPLICATION_JSON)
				.content(Obj.writeValueAsBytes(cartRequest))
				.with(SecurityMockMvcRequestPostProcessors.jwt())).andDo(print())
				.andExpect(status().isNotFound());
	}

	@Test
	public void addItemToCartItemNotFound() throws Exception {
		ModifyCartRequest cartRequest = new ModifyCartRequest();

		User user = getDefaultUser();

		user.setUsername(Integer.toString(random.nextInt(1000)));

		user = createUser(user);

		cartRequest.setUsername(user.getUsername());
		cartRequest.setItemId(0);
		cartRequest.setQuantity(1);

		this.mockMvc.perform(post("/api/cart/addToCart")
				.contentType(MediaType.APPLICATION_JSON)
				.content(Obj.writeValueAsBytes(cartRequest))
				.with(SecurityMockMvcRequestPostProcessors.jwt())).andDo(print())
				.andExpect(status().isNotFound());
	}

	@Test
	public void addItemToCart() throws Exception {
		ModifyCartRequest cartRequest = new ModifyCartRequest();

		User user = getDefaultUser();

		user.setUsername(Integer.toString(random.nextInt(1000)));

		user = createUser(user);

		Item item = getDefaultItemList().getFirst();

		cartRequest.setUsername(user.getUsername());
		cartRequest.setItemId(item.getId());
		cartRequest.setQuantity(2);

		Cart cart = new Cart();

		IntStream.range(0, cartRequest.getQuantity())
				.forEach(i -> cart.addItem(item));

		MvcResult result = this.mockMvc.perform(post("/api/cart/addToCart")
				.contentType(MediaType.APPLICATION_JSON)
				.content(Obj.writeValueAsBytes(cartRequest))
				.with(SecurityMockMvcRequestPostProcessors.jwt())).andDo(print())
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andReturn();

		Cart resultCart = Obj.readValue(result.getResponse().getContentAsString(), Cart.class);
		cart.setId(resultCart.getId());

		assertThat(cart).usingRecursiveComparison().isEqualTo(resultCart);
	}

	@Test
	public void removeItemFromCartUserNotFound() throws Exception {
		ModifyCartRequest cartRequest = new ModifyCartRequest();

		cartRequest.setUsername("nowhere");
		cartRequest.setItemId(1);
		cartRequest.setQuantity(1);

		this.mockMvc.perform(post("/api/cart/removeFromCart")
				.contentType(MediaType.APPLICATION_JSON)
				.content(Obj.writeValueAsBytes(cartRequest))
				.with(SecurityMockMvcRequestPostProcessors.jwt())).andDo(print())
				.andExpect(status().isNotFound());
	}

	@Test
	public void removeItemFromCartItemNotFound() throws Exception {
		ModifyCartRequest cartRequest = new ModifyCartRequest();

		User user = getDefaultUser();

		user.setUsername(Integer.toString(random.nextInt(1000)));

		user = createUser(user);

		cartRequest.setUsername(user.getUsername());
		cartRequest.setItemId(0);
		cartRequest.setQuantity(1);

		this.mockMvc.perform(post("/api/cart/removeFromCart")
				.contentType(MediaType.APPLICATION_JSON)
				.content(Obj.writeValueAsBytes(cartRequest))
				.with(SecurityMockMvcRequestPostProcessors.jwt())).andDo(print())
				.andExpect(status().isNotFound());
	}

	@Test
	public void removeItemFromCart() throws Exception {
		ModifyCartRequest cartRequest = new ModifyCartRequest();

		User user = getDefaultUser();

		user.setUsername(Integer.toString(random.nextInt(1000)));

		user = createUser(user);

		Item item = getDefaultItemList().getLast();

		cartRequest.setQuantity(2);
		cartRequest.setUsername(user.getUsername());
		cartRequest.setItemId(item.getId());

		Cart cart = new Cart();

		for (int i = 0; i < cartRequest.getQuantity(); i++) {
			cart.addItem(item);
		}

		cart = addToCart(cartRequest);

		cartRequest.setQuantity(1);

		cart.removeItem(item);

		MvcResult result = this.mockMvc.perform(post("/api/cart/removeFromCart")
				.contentType(MediaType.APPLICATION_JSON)
				.content(Obj.writeValueAsBytes(cartRequest))
				.with(SecurityMockMvcRequestPostProcessors.jwt())).andDo(print())
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andReturn();

		Cart resultCart = Obj.readValue(result.getResponse().getContentAsString(), Cart.class);
		cart.setId(resultCart.getId());

		assertThat(cart).usingRecursiveComparison().isEqualTo(resultCart);
	}

	@Test
	public void submitOrderUserNotFound() throws Exception {
		this.mockMvc.perform(post("/api/order/submit/{username}", "nowhere")
				.with(SecurityMockMvcRequestPostProcessors.jwt())).andDo(print())
				.andExpect(status().isNotFound());
	}

	@Test
	public void submitOrder() throws Exception {
		ModifyCartRequest cartRequest = new ModifyCartRequest();

		User user = getDefaultUser();

		user.setUsername(Integer.toString(random.nextInt(1000)));

		user = createUser(user);

		Item item = getDefaultItemList().getLast();

		cartRequest.setQuantity(2);
		cartRequest.setUsername(user.getUsername());
		cartRequest.setItemId(item.getId());

		Cart cart = new Cart();

		for (int i = 0; i < cartRequest.getQuantity(); i++) {
			cart.addItem(item);
		}

		cart = addToCart(cartRequest);

		MvcResult result = this.mockMvc.perform(post("/api/order/submit/{username}", user.getUsername())
				.with(SecurityMockMvcRequestPostProcessors.jwt())).andDo(print())
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andReturn();

		UserOrder userOrderResult = Obj.readValue(result.getResponse().getContentAsString(), UserOrder.class);

		UserOrder userOrder = UserOrder.createFromCart(cart);
		userOrder.setUser(user);

		userOrder.setId(userOrderResult.getId());

		assertThat(userOrder).usingRecursiveComparison().isEqualTo(userOrderResult);
	}

	@Test
	public void getOrderHistoryUserNotFound() throws Exception {
		this.mockMvc.perform(get("/api/order/history/{username}", "nowhere")
				.with(SecurityMockMvcRequestPostProcessors.jwt())).andDo(print())
				.andExpect(status().isNotFound());
	}

	@Test
	public void getOrderHistory() throws Exception {
		ModifyCartRequest cartRequest = new ModifyCartRequest();

		User user = getDefaultUser();

		user.setUsername(Integer.toString(random.nextInt(1000)));

		user = createUser(user);

		Item item = getDefaultItemList().getLast();

		cartRequest.setQuantity(2);
		cartRequest.setUsername(user.getUsername());
		cartRequest.setItemId(item.getId());

		Cart cart = new Cart();

		for (int i = 0; i < cartRequest.getQuantity(); i++) {
			cart.addItem(item);
		}

		addToCart(cartRequest);

		MvcResult submitResult = this.mockMvc.perform(post("/api/order/submit/{username}", user.getUsername())
				.with(SecurityMockMvcRequestPostProcessors.jwt())).andDo(print())
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andReturn();

		UserOrder userOrderResult = Obj.readValue(submitResult.getResponse().getContentAsString(), UserOrder.class);

		MvcResult historyResult = this.mockMvc.perform(get("/api/order/history/{username}", user.getUsername())
				.with(SecurityMockMvcRequestPostProcessors.jwt())).andDo(print())
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andReturn();

		List<UserOrder> userOrderHistoryResult = Obj.readValue(historyResult.getResponse().getContentAsString(),
				new TypeReference<>() {

				});

		assertEquals(userOrderHistoryResult.size(), 1);
		assertThat(userOrderResult).usingRecursiveComparison().isEqualTo(userOrderHistoryResult.getFirst());
	}

	private Cart addToCart(ModifyCartRequest cartRequest) throws Exception {
		MvcResult result = this.mockMvc.perform(post("/api/cart/addToCart")
				.contentType(MediaType.APPLICATION_JSON)
				.content(Obj.writeValueAsBytes(cartRequest))
				.with(SecurityMockMvcRequestPostProcessors.jwt())).andDo(print())
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andReturn();

		return Obj.readValue(result.getResponse().getContentAsString(), Cart.class);
	}

	private User createUser(User user) throws Exception {
		CreateUserRequest createUserRequest = new CreateUserRequest();

		createUserRequest.setUsername(user.getUsername());
		createUserRequest.setPassword(user.getPassword());
		createUserRequest.setConfirmPassword(createUserRequest.getPassword());

		MvcResult result = this.mockMvc.perform(post("/api/user/create")
				.contentType(MediaType.APPLICATION_JSON)
				.content(Obj.writeValueAsBytes(createUserRequest))).andReturn();

		return Obj.readValue(result.getResponse().getContentAsString(), User.class);
	}

	private List<Item> getDefaultItemList() {
		List<Item> items = new ArrayList<>();

		Item item1 = new Item();

		item1.setId((long) 1);
		item1.setName("Round Widget");
		item1.setPrice(BigDecimal.valueOf(2.99));
		item1.setDescription("A widget that is round");

		items.add(item1);

		Item item2 = new Item();

		item2.setId((long) 2);
		item2.setName("Square Widget");
		item2.setPrice(BigDecimal.valueOf(1.99));
		item2.setDescription("A widget that is square");

		items.add(item2);

		return items;
	}

	private User getDefaultUser() {
		User user = new User();
		user.setUsername("test");
		user.setPassword("Test@12345");

		return user;
	}
}
