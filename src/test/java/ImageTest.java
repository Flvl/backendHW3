import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.Base64;

import static io.restassured.RestAssured.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;

public class ImageTest extends BaseTest {
    private final String PathToJpgImage="src/test/resources/test_JPG.JPG";
    private final String PathTo1pixImage="src/test/resources/1pix.jpg";
    String ImageId_Jpg;
    String ImageDeleteHash_Jpg;
    private final String PathToGifImage="src/test/resources/test_GIF.gif";
    String ImageId_GIF;
    private final String PathToTXT="src/test/resources/test_txt.txt";
    private final String PathToVideo="src/test/resources/test_mp4.mp4";
    String VideoId_mp4;
    String UploadURL = "https://api.imgur.com/3/upload";
    String FavoriteURL = "https://api.imgur.com/3/image/{Hash}/favorite";

    @Test
    void UploadFileJPGImageTest(){
        Response response = given()
                .headers("Authorization", token)
                .multiPart("image",new File(PathToJpgImage))
                .formParam("title","image Title")
                .when()
                .post(UploadURL)
                .prettyPeek();
        assertThat(response.jsonPath().get("success"),equalTo(true));
        assertThat(response.jsonPath().get("status"),equalTo(200));
        ImageId_Jpg=response.jsonPath().get("data.id");
        ImageDeleteHash_Jpg=response.jsonPath().get("data.deletehash");
        assertThat(ImageId_Jpg,notNullValue());
        System.out.println(ImageId_Jpg);
    }

    @Test
    void UploadFileGIFImageTest(){
        Response response = given()
                .headers("Authorization", token)
                .multiPart("image",new File(PathToGifImage))
                .formParam("title","image Title")
                .when()
                .post(UploadURL)
                .prettyPeek();
        assertThat(response.jsonPath().get("success"),equalTo(true));
        assertThat(response.jsonPath().get("status"),equalTo(200));
        ImageId_GIF=response.jsonPath().get("data.id");
        assertThat(ImageId_GIF,notNullValue());
    }

    @Test
    void UploadFileTxtTest(){
        Response response = given()
                .headers("Authorization", token)
                .multiPart("image",new File(PathToTXT))
                .formParam("title","TXT Title")
                .when()
                .post(UploadURL)
                .prettyPeek();
        assertThat(response.jsonPath().get("status"),equalTo(400));

    }

    @Test
    void UploadFileVideoTest(){
        Response response = given()
                .headers("Authorization", token)
                .multiPart("video",new File(PathToVideo))
                .formParam("title","image Title")
                .when()
                .post(UploadURL)
                .prettyPeek();
        assertThat(response.jsonPath().get("success"),equalTo(true));
        assertThat(response.jsonPath().get("status"),equalTo(200));
        VideoId_mp4=response.jsonPath().get("data.id");
        assertThat(VideoId_mp4,notNullValue());
    }

    @Test
    void FavouriteAnImageTest () {
       String UploadImageId= given()
                .headers("Authorization", token)
                .multiPart("image",new File(PathToJpgImage))
                .formParam("title","image Title")
                .when()
                .post(UploadURL)
                .prettyPeek()
               .then()
               .extract()
               .response()
               .jsonPath()
               .getString("data.id");

System.out.println(UploadImageId);
        Response response = given()
                .headers("Authorization", token)
                .when()
                .post("https://api.imgur.com/3/image/"+UploadImageId+"/favorite")
                .prettyPeek();
        assertThat(response.jsonPath().get("status"),equalTo(200));
        assertThat(response.jsonPath().get("success"),equalTo(true));
        assertThat(response.jsonPath().get("data"),equalTo("favorited"));
    }

    @Test
    void UnFavouriteAnImageTest () {
        String UploadImageId= given()
                .headers("Authorization", token)
                .multiPart("image",new File(PathToJpgImage))
                .formParam("title","image Title")
                .when()
                .post(UploadURL)
                .prettyPeek()
                .then()
                .extract()
                .response()
                .jsonPath()
                .getString("data.id");

        given()
                .headers("Authorization", token)
                .when()
                .post("https://api.imgur.com/3/image/{UploadImageId}/favorite",UploadImageId)
                .prettyPeek();

        System.out.println(UploadImageId);
        Response response = given()
                .headers("Authorization", token)
                .when()
                .post("https://api.imgur.com/3/image/{UploadImageId}/favorite",UploadImageId)
                .prettyPeek();
        assertThat(response.jsonPath().get("status"),equalTo(200));
        assertThat(response.jsonPath().get("success"),equalTo(true));
        assertThat(response.jsonPath().get("data"),equalTo("unfavorited"));
    }

    @Test
    void DeleteImageTest () {
        String DeleteHashImage= given()
                .headers("Authorization", token)
                .multiPart("image",new File(PathToJpgImage))
                .formParam("title","image Title")
                .when()
                .post(UploadURL)
                .prettyPeek()
                .then()
                .extract()
                .response()
                .jsonPath()
                .getString("data.deletehash");

        System.out.println(DeleteHashImage);
        Response response = given()
                .headers("Authorization", token)
                .when()
                .delete("https://api.imgur.com/3/account/{username}/image/{DeleteHashImage}",username,DeleteHashImage)
                .prettyPeek();
        assertThat(response.jsonPath().get("data"),equalTo(true));
        assertThat(response.jsonPath().get("success"),equalTo(true));
        assertThat(response.jsonPath().get("status"),equalTo(200));
    }

    @Test
    void FavoriteDeletedImageTest () {
        Response response1= given()
                .headers("Authorization", token)
                .multiPart("image",new File(PathToJpgImage))
                .formParam("title","image Title")
                .when()
                .post(UploadURL)
                .prettyPeek();

        String DeleteHashImage=response1.jsonPath().get("data.deletehash");
        String IdImage=response1.jsonPath().get("data.id");

        System.out.println(DeleteHashImage);
                given()
                .headers("Authorization", token)
                .when()
                .delete("https://api.imgur.com/3/account/{username}/image/{DeleteHashImage}",username,DeleteHashImage)
                .prettyPeek();

                            given()
                        .headers("Authorization", token)
                        .when()
                        .post("https://api.imgur.com/3/image/{IdImage}/favorite",IdImage)
                        .prettyPeek()
                        .then()
                        .statusCode(404);
        //assertThat(response.jsonPath().get("code"),equalTo(404));
    }

    @Test
    void UploadFile1pixImageTest(){
        Response response = given()
                .headers("Authorization", token)
                .multiPart("image",new File(PathTo1pixImage))
                .formParam("title","image Title")
                .when()
                .post(UploadURL)
                .prettyPeek();
        assertThat(response.jsonPath().get("success"),equalTo(true));
        assertThat(response.jsonPath().get("status"),equalTo(200));
        assertThat(response.jsonPath().get("data.id"),notNullValue());

    }

    @Test
    void UploadFileVideoWithoutSoundTest(){
        Response response = given()
                .headers("Authorization", token)
                .multiPart("video",new File(PathToVideo))
                .formParam("title","10 seconds")
                .formParam("disable_audio","1")
                .when()
                .post(UploadURL)
                .prettyPeek();
        assertThat(response.jsonPath().get("success"),equalTo(true));
        assertThat(response.jsonPath().get("status"),equalTo(200));
        VideoId_mp4=response.jsonPath().get("data.id");
        assertThat(VideoId_mp4,notNullValue());
        assertThat(response.jsonPath().get("data.has_sound"),equalTo(false));
    }
}
