package com.example.demo.src.product;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponseStatus;
import com.example.demo.src.product.model.GetPostProductRes;
import com.example.demo.src.product.model.GetProductByCatagoryRes;
import com.example.demo.src.product.model.GetProductRes;
import com.example.demo.utils.JwtService;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;


@Service
public class ProductProvider {
    private final ProductDao productDao ;
    private final JwtService jwtService;
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    public ProductProvider(ProductDao productDao, JwtService jwtService) {
        this.productDao = productDao;
        this.jwtService = jwtService;
    }


    public  List<GetProductRes> getProducts() throws BaseException{
        try {
            List<GetProductRes> getProductRes = productDao.getProducts();
            return getProductRes;
        }
        catch (Exception exception){
            logger.error("Error!",exception);
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public List<GetProductRes> getProductsByRegionName(String regionName) throws BaseException{
        try {
            List<GetProductRes> getProductRes = productDao.getProductsByRegionName(regionName);
            return getProductRes;
        }
        catch (Exception exception){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }


    public List<GetProductByCatagoryRes> getProductsByCatagory(String catagoryName) throws BaseException{

        try{
            List<GetProductByCatagoryRes> getProductByCatagoryRes = productDao.getProductsByCatagory(catagoryName);
            return getProductByCatagoryRes;
        }catch (Exception exception){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }
    public GetPostProductRes getProduct(int productIdx) throws BaseException{

        // productIdx가 DB에 없는 경우
        boolean productExists = productDao.checkProductExists(productIdx);
        if (!productExists) {
            throw new BaseException(BaseResponseStatus.INVALID_PRODUCTID);
        }
        try{
            GetPostProductRes getPostProductRes = productDao.getProduct(productIdx);
            return getPostProductRes;
        }catch (Exception exception){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }



}
