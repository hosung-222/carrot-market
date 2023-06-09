package com.example.demo.src.product;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.config.BaseResponseStatus;
import com.example.demo.src.product.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/app/product")
public class productController {


    @Autowired
    private ProductProvider productProvider;
    @Autowired
    private ProductService productService;

    /**
     * 페이징해서 전체 상품 조회
     * @param page
     * @param pageSize
     * @return
     */
    @ResponseBody
    @GetMapping("")
    public BaseResponse<List<GetAllProductRes>> getAllProductsByPage(@RequestParam("page") int page, @RequestParam("pageSize") int pageSize){
        try{
            List<GetAllProductRes> getAllProductRes = productProvider.getProducts(page, pageSize);
            return new BaseResponse<>(getAllProductRes);
        }catch (BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }


    /**
     * 상품 리스트조회 API 1
     * @param regionName
     * 상품 지역에 따른 전체 상품 조회 API
     * [GET] /product/:regionName
     * @return BaseResponse<List<GetProductRes>
     */
    @ResponseBody
    @GetMapping("{regionName}")
    public BaseResponse<List<GetProductRes>> getProducts( @PathVariable("regionName") String regionName){
        try{
            List<GetProductRes> getProductRes = productProvider.getProductsByRegionName(regionName);
            return new BaseResponse<>(getProductRes);
        }catch (BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 상품 카테고리에 따른 상품 조회 API
     * [GET] /product/catagory/:catagoryName
     * @param catagoryName
     * @return
     */
    @ResponseBody
    @GetMapping("/catagory")
    public BaseResponse<List<GetProductByCatagoryRes>> getProductsByCatagory(@PathVariable("catagoryName") String catagoryName){
        try{
            List<GetProductByCatagoryRes> getProductByCatagoryRes= productProvider.getProductsByCatagory(catagoryName);
            return new BaseResponse<>(getProductByCatagoryRes);
        }catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 상품 상세 페이지 조회 API
     * [GET] /product/about/:productIdx
     * @param productIdx
     * @return
     */
    @ResponseBody
    @GetMapping("/about")
    public BaseResponse<GetPostProductRes> getPostProduct(@RequestParam("productIdx") int productIdx){
        try{
            GetPostProductRes getPostProductRes = productProvider.getProduct(productIdx);
            return new BaseResponse<>(getPostProductRes);
        }catch (BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 상품 등록 API
     * [POST] /product
     * @param postProductReq
     * @return
     */
    @ResponseBody
    @PostMapping("")
    public BaseResponse<PostProductRes> createProduct(@RequestBody PostProductReq postProductReq){
        if (postProductReq.getProductTitle().isEmpty() ){
            return new BaseResponse<>(BaseResponseStatus.POST_PRODUCT_EMPTY_TITLE);
        }  if (postProductReq.getProductContents().isEmpty()) {
            return new BaseResponse<>((BaseResponseStatus.POST_PRODUCT_EMPTY_CONTENT));
        }if (postProductReq.getImgUrl().isEmpty()){
            return new BaseResponse<>(BaseResponseStatus.POST_PRODUCT_EMPTY_Image);
        }if (postProductReq.getProductPrice()!=null){
            return new BaseResponse<>(BaseResponseStatus.POST_PRODUCT_EMPTY_PRICE);
        }
        try {
            PostProductRes postProductRes = productService.createProduct(postProductReq);
            return new BaseResponse<>(postProductRes);

        }catch (BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 상품 수정 API
     * [PATCH] /product/edit/:productIdx
     * @param productIdx
     * @param patchProductReq
     * @return
     */
    @ResponseBody
    @PatchMapping("/edit/{productIdx}")
    public BaseResponse<String> patchProduct(@PathVariable("productIdx") int productIdx, @RequestBody PatchProductReq patchProductReq) {
        if (patchProductReq.getProductTitle().isEmpty()) {
            return new BaseResponse<>(BaseResponseStatus.POST_PRODUCT_EMPTY_TITLE);
        }
        if (patchProductReq.getProductContents().isEmpty()) {
            return new BaseResponse<>((BaseResponseStatus.POST_PRODUCT_EMPTY_CONTENT));
        }
        if (patchProductReq.getImgUrl().isEmpty()) {
            return new BaseResponse<>(BaseResponseStatus.POST_PRODUCT_EMPTY_Image);
        }
        try {
            productService.patchProduct(productIdx, patchProductReq);

            String result = productIdx+ "번 상품이 수정되었습니다.";
            return new BaseResponse<>(result);

        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 상품 삭제 API
     * [DELETE] /product/:productIdx
     * @param productIdx
     * @return
     */
    @ResponseBody
    @DeleteMapping("{productIdx}")
    public BaseResponse<String> deleteProduct(@PathVariable("productIdx") int productIdx){
        try{
            productService.deleteProduct(productIdx);
            String result = productIdx + "번 상품이 삭제되었습니다.";
            return new BaseResponse<>(result);
        }catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 찜한 상품 취소
     * @param productIdx
     * @param userIdx
     * @return
     */
    @ResponseBody
    @DeleteMapping("")
    public BaseResponse<String> deleteLikeProduct(@RequestParam("productIdx") int productIdx,@RequestParam("userIdx") int userIdx){
        try {
            productService.deleteLikeProduct(productIdx, userIdx);
            String result = userIdx+" 유저가 " + productIdx +" 상품 좋아요를 취소 했습니다. ";
            return new BaseResponse<>(result);
        }catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 찜한 상품 등록
     * @param userIdx
     * @param productIdx
     * @return
     */
    @ResponseBody
    @PostMapping("/register")
    public BaseResponse<String> registerLikeProduct(@RequestParam("userIdx") int userIdx, @RequestParam("productIdx") int productIdx) {
        try {
            if (productService.addLikeProduct(userIdx, productIdx))
            return new BaseResponse<>(productIdx + "찜한 목록 추가");

            else return new BaseResponse<>("찜한 목록 추가 실패");
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 상품 끌어올리기
     * @param productIdx
     * @return
     */
    @ResponseBody
    @PatchMapping("/updatetime/{productIdx}")
    public BaseResponse<String> updateProductTime(@PathVariable("productIdx")int productIdx){
        try {
            if(productService.updateProductTime(productIdx))
                return new BaseResponse<>("끌어올리기 완료");
            else
                return new BaseResponse<>("끌어 올리기 실패");
        } catch (BaseException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 내가 찜한 상품 보기 API
     * @param userIdx
     * @return
     */
    @ResponseBody
    @GetMapping("/likeproduct")
    public BaseResponse<List<GetLikeProductRes>> getLikeProducts(@RequestParam("userIdx")int userIdx){
        try {
            List<GetLikeProductRes> getLikeProductRes = productProvider.getLikeProducts(userIdx);
            return new BaseResponse<>(getLikeProductRes);
        }catch (BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 키워드로 상품 검색 API
     * @param keywords
     * @return
     */
    @ResponseBody
    @GetMapping("/search")
    public BaseResponse<List<GetSearchProductRes>> getSearchProducts(@RequestParam("keywords")String keywords){
        try {
            List<GetSearchProductRes> getSearchProductRes = productProvider.getSearchProducts(keywords);
            return new BaseResponse<>(getSearchProductRes);

        }catch (BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }
}

