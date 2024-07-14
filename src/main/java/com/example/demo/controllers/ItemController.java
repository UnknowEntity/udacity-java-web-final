package com.example.demo.controllers;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.splunk.logging.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.config.ApplicationConfiguration;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;

@RestController
@RequestMapping("/api/item")
public class ItemController {

	Logger logger = LoggerFactory.getLogger(ApplicationConfiguration.getSplunkLogName());

	@Autowired
	private ItemRepository itemRepository;
	
	@GetMapping
	public ResponseEntity<List<Item>> getItems() {
		logger.info("GET_ITEMS_SUCCESS");
		return ResponseEntity.ok(itemRepository.findAll());
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<Item> getItemById(@PathVariable Long id) {
		Optional<Item> findItemResult = itemRepository.findById(id);

		if (!findItemResult.isPresent()) {
			logger.error("GET_ITEM_BY_ID_FAILED: ID_NOT_FOUND");
			return ResponseEntity.notFound().build();
		}

		logger.info("GET_ITEM_BY_ID_SUCCESS");
		return ResponseEntity.ok(findItemResult.get());
	}
	
	@GetMapping("/name/{name}")
	public ResponseEntity<List<Item>> getItemsByName(@PathVariable String name) {
		List<Item> items = itemRepository.findByName(name);

		if (items == null || items.isEmpty()) {
			logger.error("FIND_ITEMS_BY_NAME_FAILED: ITEM_NAME_NOT_FOUND");
			return ResponseEntity.notFound().build();
		}

		logger.info("FIND_ITEMS_BY_NAME_SUCCESS");
		return ResponseEntity.ok(items);
			
	}
	
}
