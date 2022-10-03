package com.example.demo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.http.MediaType
import org.springframework.web.servlet.function.RouterFunction
import org.springframework.web.servlet.function.ServerResponse
import org.springframework.web.servlet.function.router

@SpringBootApplication
class DemoApplication{

	@Bean
	fun getExampleRoute(): RouterFunction<*> = exampleRouterFunction
}

val exampleRouterFunction: RouterFunction<*> = router {
	//GET("/0", exampleHandler::handle)
	// or using a training lambda
	GET("/1") {
		ServerResponse.ok().body("Hi, this response was produced by an handler")
	}
	accept(MediaType.TEXT_PLAIN).nest {

		"functional/examples".nest {
			GET("/0") {
				ServerResponse.ok().body("Hi, this response was produced by an handler")
			}
		}
	}
}


fun main(args: Array<String>) {
	runApplication<DemoApplication>(*args)
}
