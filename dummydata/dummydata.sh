#!/usr/bin/env bash

PRODUCT_COUNT_PER_CAT=10
if [[ -n "$1" ]]; then
    PRODUCT_COUNT_PER_CAT=$1
    re='^[0-9]+$'
    if ! [[ ${PRODUCT_COUNT_PER_CAT} =~ $re ]] ; then
       echo "error: Not a number '$PRODUCT_COUNT_PER_CAT'" >&2; exit 1
    fi
fi

CATALOG=$(curl -s -X POST "http://localhost:8080/catalogs" -H  "accept: application/hal+json" -H  "Content-Type: application/json" -d "{\"name\":\"My awesome catalog\"}" | jq "._links.self.href")

# Root category
CLOTHES=$(curl -s -X POST "http://localhost:8080/categories" -H  "accept: application/hal+json" -H  "Content-Type: application/json" -d "{\"name\":\"Clothes\"}" | jq "._links.self.href")
# Sub-category
MEN=$(curl -s -X POST "http://localhost:8080/categories" -H  "accept: application/hal+json" -H  "Content-Type: application/json" -d "{\"name\":\"Men\",\"parents\": [$CLOTHES]}" | jq "._links.self.href")
WOMEN=$(curl -s -X POST "http://localhost:8080/categories" -H  "accept: application/hal+json" -H  "Content-Type: application/json" -d "{\"name\":\"Women\",\"parents\": [$CLOTHES]}}" | jq "._links.self.href")
# Category with data
T_SHIRT=$(curl -s -X POST "http://localhost:8080/categories" -H  "accept: application/hal+json" -H  "Content-Type: application/json" -d "{\"name\":\"T-shirt\",\"parents\": [$MEN,$WOMEN]}" | jq "._links.self.href")
JACKET=$(curl -s -X POST "http://localhost:8080/categories" -H  "accept: application/hal+json" -H  "Content-Type: application/json" -d "{\"name\":\"Jacket\",\"parents\": [$MEN,$WOMEN]}" | jq "._links.self.href")
SHIRT=$(curl -s -X POST "http://localhost:8080/categories" -H  "accept: application/hal+json" -H  "Content-Type: application/json" -d "{\"name\":\"Shirt\",\"parents\": [$MEN]}" | jq "._links.self.href")
DRESS=$(curl -s -X POST "http://localhost:8080/categories" -H  "accept: application/hal+json" -H  "Content-Type: application/json" -d "{\"name\":\"Dress\",\"parents\": [$WOMEN]}" | jq "._links.self.href")

# Sizes (extra-category)
SIZE_S=$(curl -s -X POST "http://localhost:8080/categories" -H  "accept: application/hal+json" -H  "Content-Type: application/json" -d "{\"name\":\"Small\",\"parents\": [$CLOTHES]}" | jq "._links.self.href")
SIZE_M=$(curl -s -X POST "http://localhost:8080/categories" -H  "accept: application/hal+json" -H  "Content-Type: application/json" -d "{\"name\":\"Medium\",\"parents\": [$CLOTHES]}" | jq "._links.self.href")
SIZE_L=$(curl -s -X POST "http://localhost:8080/categories" -H  "accept: application/hal+json" -H  "Content-Type: application/json" -d "{\"name\":\"Large\",\"parents\": [$CLOTHES]}" | jq "._links.self.href")
SIZE_XL=$(curl -s -X POST "http://localhost:8080/categories" -H  "accept: application/hal+json" -H  "Content-Type: application/json" -d "{\"name\":\"Extra large\",\"parents\": [$CLOTHES]}" | jq "._links.self.href")
SIZE_XXL=$(curl -s -X POST "http://localhost:8080/categories" -H  "accept: application/hal+json" -H  "Content-Type: application/json" -d "{\"name\":\"Extra-Extra large\",\"parents\": [$CLOTHES]}" | jq "._links.self.href")

WAREHOUSE=$(curl -s -X POST "http://localhost:8080/warehouses" -H  "accept: application/hal+json" -H  "Content-Type: application/json" -d "{\"name\":\"My own warehouse\"}" | jq "._links.self.href")

function_generate_clothes_with_stocks () {
    num=$1
    sub_category=$2
    data_category=$3
    name=$4
    for pos in $(seq 1 ${num})
    do
        dice_roll=$(( ( RANDOM % 100 )  + 1 )) # Slightly biased
        size=${SIZE_XXL}
        size_name="XXL"
        if [[ dice_roll -lt 80 ]]; then
            size=${SIZE_XL}
            size_name="XL"
        fi
        if [[ dice_roll -lt 60 ]]; then
            size=${SIZE_L}
            size_name="L"
        fi
        if [[ dice_roll -lt 40 ]]; then
            size=${SIZE_M}
            size_name="M"
        fi
        if [[ dice_roll -lt 20 ]]; then
            size=${SIZE_S}
            size_name="S"
        fi

        product_name="$name $size_name"

        if [[ -n "$sub_category" ]]; then
            product=$(curl -s -X POST "http://localhost:8080/products" -H  "accept: application/hal+json" -H  "Content-Type: application/json" -d "{\"name\":\"$product_name\",\"catalogs\":[$CATALOG],\"categories\":[$sub_category,$data_category,$size]}"  | jq "._links.self.href")
        else
            product=$(curl -s -X POST "http://localhost:8080/products" -H  "accept: application/hal+json" -H  "Content-Type: application/json" -d "{\"name\":\"$product_name\",\"catalogs\":[$CATALOG],\"categories\":[$data_category,$size]}"  | jq "._links.self.href")
        fi

        dice_roll=$(( ( RANDOM % 10 )  + 1 )) # Slightly biased
        stock=0
        if [[ dice_roll -gt 3 ]]; then
            stock=$(($dice_roll - 3))
        fi
        curl -s -X POST "http://localhost:8080/stocks" -H  "accept: application/hal+json" -H  "Content-Type: application/json" -d "{\"product\":$product,\"warehouse\":$WAREHOUSE,\"balance\":$stock}" &>/dev/null
    done
}

function_generate_clothes_with_stocks ${PRODUCT_COUNT_PER_CAT} ${MEN} ${T_SHIRT} "Men's T-Shirt"
function_generate_clothes_with_stocks ${PRODUCT_COUNT_PER_CAT} ${WOMEN} ${T_SHIRT} "Women's T-Shirt"

function_generate_clothes_with_stocks ${PRODUCT_COUNT_PER_CAT} ${MEN} ${JACKET} "Men's Jacket"
function_generate_clothes_with_stocks ${PRODUCT_COUNT_PER_CAT} ${WOMEN} ${JACKET} "Women's Jacket"

function_generate_clothes_with_stocks ${PRODUCT_COUNT_PER_CAT} "" ${SHIRT} "Men's Shirt"
function_generate_clothes_with_stocks ${PRODUCT_COUNT_PER_CAT} "" ${DRESS} "Women's Dress"

