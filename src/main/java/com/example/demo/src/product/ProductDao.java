package com.example.demo.src.product;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponseStatus;
import com.example.demo.src.product.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.List;

@Repository
public class ProductDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }


    public int getTotalProducts(){
        String totalProductsQuery = "SELECT count(*) from product where hide = 0";
        return jdbcTemplate.queryForObject(totalProductsQuery, Integer.class);
    }

    // 전체 상품 조회
    public List<GetAllProductRes> getProducts(int pageSize, int offset) throws BaseException {

        String sql = "SELECT R.region_name AS regionName, P.product_title AS productTitle, P.product_price AS productPrice, P.updated_at AS updatedAt, " +
                "COALESCE(MAX(i.img_url), '기본 이미지 URL') AS imgUrl, COUNT(lP.user_idx) AS cntLike " +
                "FROM product P " +
                "JOIN region R ON P.region_idx = R.region_idx " +
                "LEFT JOIN image i ON P.product_idx = i.product_idx " +
                "LEFT JOIN like_product lP ON P.product_idx = lP.product_idx " +
                "WHERE P.hide = 0 " +
                "GROUP BY R.region_name, P.product_title, P.product_price, P.updated_at " +
                "ORDER BY P.updated_at DESC " +
                "LIMIT ? OFFSET ?";

        return jdbcTemplate.query(sql, new Object[]{pageSize, offset}, (rs, rowNum) -> {
            GetAllProductRes product = new GetAllProductRes();
            // 결과셋에서 필요한 데이터를 가져와 ProductDTO에 설정
            product.setRegionName(rs.getString("regionName"));
            product.setProductTitle(rs.getString("productTitle"));
            product.setProductPrice(rs.getInt("productPrice"));
            product.setUpdatedAt(rs.getTimestamp("updatedAt"));
            product.setImgUrl(rs.getString("imgUrl"));
            product.setCntLike(rs.getInt("cntLike"));
            return product;
        });
    }

    // regionName에 따른 상품 조회 (완료)
    public List<GetProductRes> getProductsByRegionName(String regionName) {
        String getProductsByRegionNameQuery = "SELECT R.region_name as regionName, P.product_title as productTitle, P.product_price as productPrice, P.updated_at as updatedAt,\n" +
                "\t       COALESCE(MAX(i.img_url), '기본 이미지 URL') AS imgUrl, count(lP.user_idx) AS cntLike , R.region_name as regionName\n" +
                "FROM product P\n" +
                "JOIN region R ON P.region_idx = R.region_idx\n" +
                "LEFT JOIN image i ON P.product_idx = i.product_idx\n" +
                "LEFT JOIN like_product lP on P.product_idx = lP.product_idx\n" +
                "WHERE R.region_name = ? AND P.hide = 0\n" +
                "GROUP BY R.region_name, P.product_title, P.product_price, P.updated_at";
        String getProductByRegionParams = regionName;
        return this.jdbcTemplate.query(getProductsByRegionNameQuery,
                (rs, rowNum) -> new GetProductRes(
                        rs.getString("productTitle"),
                        rs.getInt("productPrice"),
                        rs.getString("regionName"),
                        rs.getTimestamp("updatedAt"),
                        rs.getString("imgUrl"),
                        rs.getInt("cntLike")),
                getProductByRegionParams);
    }

    //catagoryIdx에 따른 상품 조회
    public List<GetProductByCatagoryRes> getProductsByCatagory(String catagoryName){
        String getProductsByCatgoryQuery = "SELECT R.region_name as regionName, P.product_title as productTitle, P.product_price as productPrice, P.updated_at as updatedAt,\n" +
                "\t       COALESCE(MAX(i.img_url), '기본 이미지 URL') AS imgUrl, count(lP.user_idx) AS cntLike, R.region_name as regionName, pc.catagory_name as catagoryName\n" +
                "FROM product P\n" +
                "JOIN region R ON P.region_idx = R.region_idx\n" +
                "LEFT JOIN image i ON P.product_idx = i.product_idx\n" +
                "LEFT JOIN like_product lP on P.product_idx = lP.product_idx\n" +
                "LEFT JOIN product_catagory pc on P.productcatagory_idx = pc.productcatagory_idx\n" +
                "WHERE pc.catagory_name = ? and P.hide = 0\n" +
                "GROUP BY R.region_name, P.product_title, P.product_price, P.updated_at;";
        String getProductsByCatagoryParams = catagoryName;
        return this.jdbcTemplate.query(getProductsByCatgoryQuery,
                (rs , rowNum) -> new GetProductByCatagoryRes(
                        rs.getString("catagoryName"),
                        rs.getString("productTitle"),
                        rs.getInt("productPrice"),
                        rs.getString("regionName"),
                        rs.getTimestamp("updatedAt"),
                        rs.getString("imgUrl"),
                        rs.getInt("cntLike")),
                getProductsByCatagoryParams);
    }

    //상품 상세 페이지 조회 (완료)
    public GetPostProductRes getProduct(int productIdx){
        String getProductQuery = "SELECT P.product_title AS productTitle, P.product_price AS productPrice, "+
                "P.product_contents AS productContents, P.view_cnt AS viewCnt,P.price_offer AS priceOffer, "+
                "P.product_status AS productStatus, P.updated_at AS updatedAt, U.user_name AS userName, "+
                "U.user_degree AS userDegree, "+
                "R.region_name AS regionName, "+
                "pC.catagory_name AS catagoryName, "+
                "IFNULL(COUNT(lP.product_idx),0) AS likeProductCnt, "+
                "GROUP_CONCAT(COALESCE(i.img_url, '기본 이미지 URL'), '\\|') AS imgUrls "+
                "FROM product P "+
                "LEFT JOIN user U ON P.user_idx= U.user_idx "+
                "LEFT JOIN region R ON P.region_idx = R.region_idx "+
                "LEFT JOIN product_catagory pC ON P.productcatagory_idx= pC.productcatagory_idx "+
                " LEFT JOIN like_product lP ON P.product_idx = lP.product_idx "+
                "LEFT JOIN image i ON i.product_idx = P.product_idx "+
                "WHERE P.product_idx = ? "+
                "GROUP BY P.product_idx ";

        int getProductParams = productIdx;
        return this.jdbcTemplate.queryForObject(getProductQuery,
                (rs, rowNum) -> new GetPostProductRes(
                        rs.getString("productTitle"),
                        rs.getInt("productPrice"),
                        rs.getString("productContents"),
                        rs.getInt("viewCnt"),
                        rs.getString("priceOffer"),
                        rs.getInt("productStatus"),
                        rs.getTimestamp("updatedAt"),
                        rs.getString("userName"),
                        rs.getDouble("userDegree"),
                        rs.getString("regionName"),
                        rs.getString("catagoryName"),
                        Arrays.asList(rs.getString("imgUrls").split("\\|,")),
                        rs.getInt("likeProductCnt")),
                getProductParams);
    }
    public int createProduct(PostProductReq postProductReq) {
        String createProdctQuery = "insert into product(product_title, product_price, product_contents, price_offer, region_idx, user_idx, productcatagory_idx) " +
                "VALUE (?,?,?,?,?,?,?)";
        Object[] createProductParams =
                new Object[]{postProductReq.getProductTitle(),
                        postProductReq.getProductPrice(),
                        postProductReq.getProductContents(),
                        postProductReq.getPriceOffer(),
                        postProductReq.getRegionIdx(),
                        postProductReq.getUserIdx(),
                        postProductReq.getProductCatagoryIdx()};
        this.jdbcTemplate.update(createProdctQuery, createProductParams);

        String lastInsertIdQuery = "select last_insert_id()";
        int productId = jdbcTemplate.queryForObject(lastInsertIdQuery, int.class);

        String createImageQuery = "insert into image (product_idx, img_url) values (?,?)";
        for (String imageUrl : postProductReq.getImgUrl()) {
            Object[] createProductImageParams = new Object[]{productId, imageUrl};
            jdbcTemplate.update(createImageQuery, createProductImageParams);
        }

        return this.jdbcTemplate.queryForObject(lastInsertIdQuery,int.class);
    }

    public int patchProduct(int productIdx, PatchProductReq patchProductReq){
        String patchProductQuery = "update product\n" +
                "set product_title = ?, " +
                "product_price = ?, p" +
                "roduct_contents = ?, " +
                "productcatagory_idx = ?, " +
                "price_offer = ?, " +
                "region_idx = ?\n" +
                "where product_idx = ?;";

        Object[] patchProductParams = new Object[]{
                patchProductReq.getProductTitle(),
                patchProductReq.getProductPrice(),
                patchProductReq.getProductContents(),
                patchProductReq.getProductCatagoryIdx(),
                patchProductReq.getPriceOffer(),
                patchProductReq.getRegionIdx(),
                productIdx
                };
        this.jdbcTemplate.update(patchProductQuery,patchProductParams);

        String deleteImageQuery = "delete from image where product_idx = ?";
        jdbcTemplate.update(deleteImageQuery, productIdx);

        String lastInsertIdQuery = "select last_insert_id()";
        int productId = jdbcTemplate.queryForObject(lastInsertIdQuery, int.class);


        String patchImageQuery = "INSERT INTO image(product_idx, img_url) VALUES (?, ?);";
        for(String imgUrl : patchProductReq.getImgUrl()){
            Object[] patchImageParams = new Object[]{productIdx, imgUrl};
            jdbcTemplate.update(patchImageQuery, patchImageParams);
        }
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery,int.class);
    }

    public boolean checkProductExists(int productIdx) {
        String query = "SELECT COUNT(*) FROM product WHERE product_idx = ?";
        Object[] params = new Object[]{productIdx};
        int count = jdbcTemplate.queryForObject(query, params, Integer.class);
        return count > 0;
    }

    public int deleteProduct(int productIdx){
        String deleteImageQuery = "delete from image where product_idx = ?";
        int deleteIamgeParams = productIdx;
        this.jdbcTemplate.update(deleteImageQuery,deleteIamgeParams);

        String deleteProductQuery = "delete from product where product_idx = ?";
        int deleteProdutParams = productIdx;
        return this.jdbcTemplate.update(deleteProductQuery,deleteProdutParams);
    }
    public boolean checkLikeProductExists(int productIdx, int userIdx){
        String query = "select count(*) from like_product where user_idx = ? and product_idx = ?";
        Object[] params = new Object[]{userIdx, productIdx};
        int count = jdbcTemplate.queryForObject(query,params,Integer.class);
        return count>0;
    }

    public int deleteLikeProduct(int productIdx, int userIdx){
        String deleteLikeProductQuery = "delete from like_product where user_idx=? and product_idx = ?";
        Object[] params = new Object[]{userIdx, productIdx};
        return this.jdbcTemplate.update(deleteLikeProductQuery,params);
    }
    public int addLikeProduct(int userIdx, int produxtIdx){
        String addLikeProductQuery = "INSERT INTO like_product (user_idx, product_idx) VALUES (?, ?)";
        Object[] addLikeProductParams = new Object[]{userIdx,produxtIdx};
        return this.jdbcTemplate.update(addLikeProductQuery,addLikeProductParams);
    }
}
