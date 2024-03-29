package com.wang.productcenter.entity.BO;


import com.wang.fastfood.apicommons.entity.DTO.DetailTypeDTO;
import com.wang.fastfood.apicommons.entity.common.Page;
import com.wang.fastfood.apicommons.entity.common.convert.DTOConvert;
import com.wang.fastfood.apicommons.entity.common.convert.POConvert;
import com.wang.productcenter.entity.PO.DetailTypePO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.BeanUtils;

/**
 * 
 * 
 * @author wAnG
 * @Date  2021-11-24 00:15:32
 * @Description: 可选项种类（商品可选项,温度，甜度，配料等等）
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DetailType extends Page {

	/**
	 * 商品详情分类ID
	 */
	private Integer id;
	/**
	 * 商品详情ID
	 */
	private Integer productDetailId;
	/**
	 * 商品详情分类名称
	 */
	private String detailTypeName;

	/**
	 * 价格
	 */
	private double price;

	/**
	 * 商品详情
	 */
	private ProductDetail productDetail;

	public DetailTypePO doForward(){
		DetailTypePOConvert convert = new DetailTypePOConvert();
		return convert.convert(this);
	}

	public DetailTypeDTO doBackward(){
		DetailTypeDTOConvert convert = new DetailTypeDTOConvert();
		return convert.convert(this);
	}


	private static class DetailTypePOConvert implements POConvert<DetailType, DetailTypePO>{

		@Override
		public DetailTypePO convert(DetailType detailType) {
			DetailTypePO detailTypePO = new DetailTypePO();
			BeanUtils.copyProperties(detailType,detailTypePO);
			return detailTypePO;
		}
	}

	private static class DetailTypeDTOConvert implements DTOConvert<DetailType,DetailTypeDTO>{

		@Override
		public DetailTypeDTO convert(DetailType detailType) {
			DetailTypeDTO detailTypeDTO = new DetailTypeDTO();
			BeanUtils.copyProperties(detailType,detailTypeDTO);
			if(detailType.getProductDetail() != null){
				detailTypeDTO.setProductDetailDTO(detailType.getProductDetail().doBackward());
			}
			return detailTypeDTO;
		}
	}


}
