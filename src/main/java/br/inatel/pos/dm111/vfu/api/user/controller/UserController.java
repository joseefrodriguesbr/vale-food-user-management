package br.inatel.pos.dm111.vfu.api.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController
{
	@GetMapping(value = "/hello")
	public ResponseEntity<?> hello()
	{
		return ResponseEntity.ok("Hello World!");
	}
}
