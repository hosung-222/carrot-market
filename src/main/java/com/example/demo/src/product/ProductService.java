package com.example.demo.src.product;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponseStatus;
import com.example.demo.src.product.model.PatchProductReq;
import com.example.demo.src.product.model.PostProductReq;
import com.example.demo.src.product.model.PostProductRes;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

    private final ProductDao productDao;
    private final ProductProvider productProvider;

    public ProductService(ProductDao productDao, ProductProvider productProvider) {
        this.productDao = productDao;
        this.productProvider = productProvider;
    }


    //POST
    public PostProductRes createProduct(PostProductReq postProductRes) throws BaseException{
        try {
            int productIdx  = productDao.createProduct(postProductRes);
            return new PostProductRes(productIdx);

        }catch (Exception exception){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);

        }
    }

    public void patchProduct(int productIdx, PatchProductReq patchProductReq) throws BaseException{
        try {
            int result = productDao.patchProduct(productIdx, patchProductReq);
            if (result == 0){
                throw new BaseException(BaseResponseStatus.MODIFY_FAIL_PRODUCT);
            }
        }catch (Exception exception){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }
    public void deleteProduct(int productIdx) throws BaseException {
        boolean productExists = productDao.checkProductExists(productIdx);
        if (!productExists) {
            throw new BaseException(BaseResponseStatus.INVALID_PRODUCTID);
        }
//        try {
            int result = productDao.deleteProduct(productIdx);
//        }catch (Exception exception){
//            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
//        }
    }
    public void deleteLikeProduct(int productIdx, int userIdx)throws BaseException{
        boolean likeProductExists = productDao.checkLikeProductExists(productIdx,userIdx);
        if(!likeProductExists){
            throw new BaseException(BaseResponseStatus.INVALID_PRODUCTID_OR_USERID);
        }
        int result = productDao.deleteLikeProduct(productIdx, userIdx);
    }

    public boolean addLikeProduct (int userIdx, int productIdx) throws BaseException{
        if(productDao.addLikeProduct(userIdx, productIdx)>0){
            return true;
        }else return false;
    }


    // 48 시간 설정 필요
    public boolean updateProductTime(int productIdx) throws BaseException{

        if(productDao.updateProductTime(productIdx)>0){
            return true;
        }
        else return false;
    }


}
