package com.pblgllgs.productsmicroservice.service;

import com.pblgllgs.productsmicroservice.rest.CreateProductRestModel;

public interface ProductService {

    String createProduct(CreateProductRestModel productRestModel) throws Exception;
}
