package com.wang.tradecenter.fegin;

import com.wang.fastfood.apicommons.entity.DTO.DetailTypeDTO;
import com.wang.fastfood.apicommons.entity.DTO.ProductDTO;
import com.wang.fastfood.apicommons.entity.DTO.UpdateProductDetail;
import com.wang.fastfood.apicommons.entity.common.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @Auther: wAnG
 * @Date: 2022/3/21 19:38
 * @Description:
 */

@Service
@FeignClient(name = "Product-Center")
public interface ProductClient {

    @PostMapping("/product/getById")
    Response<ProductDTO> getProductById(Integer product);

    @PostMapping("/product/getByIds")
    Response<List<ProductDTO>> getProductByIds(List<Integer> product);

    @PostMapping("/detailType/getByIds")
    Response<List<DetailTypeDTO>> getDetailTypeByIds(List<Integer> detailTypeIds);

    @PostMapping("/product/updateSales")
    void updateSales(@RequestBody List<UpdateProductDetail> orderDetails);

}
