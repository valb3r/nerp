#!/usr/bin/env bash

CATALOG=$(curl -s -X POST "http://localhost:8080/catalogs" -H  "accept: application/hal+json" -H  "Content-Type: application/json" -d "{\"name\":\"My awesome catalog\"}" | jq "._links.self.href")

CLOTHES=$(curl -s -X POST "http://localhost:8080/categories" -H  "accept: application/hal+json" -H  "Content-Type: application/json" -d "{\"name\":\"Clothes\"}" | jq "._links.self.href")
MEN=$(curl -s -X POST "http://localhost:8080/categories" -H  "accept: application/hal+json" -H  "Content-Type: application/json" -d "{\"name\":\"Men\",\"parents\": [$CLOTHES]}" | jq "._links.self.href")
WOMEN=$(curl -s -X POST "http://localhost:8080/categories" -H  "accept: application/hal+json" -H  "Content-Type: application/json" -d "{\"name\":\"Women\",\"parents\": [$CLOTHES]}}" | jq "._links.self.href")
T_SHIRT=$(curl -s -X POST "http://localhost:8080/categories" -H  "accept: application/hal+json" -H  "Content-Type: application/json" -d "{\"name\":\"T-shirt\",\"parents\": [$MEN,$WOMEN]}" | jq "._links.self.href")
JACKET=$(curl -s -X POST "http://localhost:8080/categories" -H  "accept: application/hal+json" -H  "Content-Type: application/json" -d "{\"name\":\"Jacket\",\"parents\": [$MEN,$WOMEN]}" | jq "._links.self.href")
DRESS=$(curl -s -X POST "http://localhost:8080/categories" -H  "accept: application/hal+json" -H  "Content-Type: application/json" -d "{\"name\":\"Dress\",\"parents\": [$WOMEN]}" | jq "._links.self.href")
SHIRT=$(curl -s -X POST "http://localhost:8080/categories" -H  "accept: application/hal+json" -H  "Content-Type: application/json" -d "{\"name\":\"Shirt\",\"parents\": [$MEN]}" | jq "._links.self.href")

# Sizes
SIZE_S=$(curl -s -X POST "http://localhost:8080/categories" -H  "accept: application/hal+json" -H  "Content-Type: application/json" -d "{\"name\":\"Small\",\"parents\": [$CLOTHES]}" | jq "._links.self.href")
SIZE_M=$(curl -s -X POST "http://localhost:8080/categories" -H  "accept: application/hal+json" -H  "Content-Type: application/json" -d "{\"name\":\"Medium\",\"parents\": [$CLOTHES]}" | jq "._links.self.href")
SIZE_L=$(curl -s -X POST "http://localhost:8080/categories" -H  "accept: application/hal+json" -H  "Content-Type: application/json" -d "{\"name\":\"Large\",\"parents\": [$CLOTHES]}" | jq "._links.self.href")
SIZE_XL=$(curl -s -X POST "http://localhost:8080/categories" -H  "accept: application/hal+json" -H  "Content-Type: application/json" -d "{\"name\":\"Extra large\",\"parents\": [$CLOTHES]}" | jq "._links.self.href")
SIZE_XXL=$(curl -s -X POST "http://localhost:8080/categories" -H  "accept: application/hal+json" -H  "Content-Type: application/json" -d "{\"name\":\"Extra-Extra large\",\"parents\": [$CLOTHES]}" | jq "._links.self.href")