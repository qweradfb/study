package cn.itcast.jwt;


import com.alibaba.fastjson.JSON;
import com.sun.java.swing.plaf.windows.resources.windows;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaSigner;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;

import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.util.HashMap;
import java.util.Map;

public class JwtTest {
    @Test
    public void Test(){
        //证书文件
        String key_location = "xc.keystore";
        //密钥库密码
        String keystore_password = "xuechengkeystore";
        ClassPathResource classPathResource = new ClassPathResource(key_location);
        KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(classPathResource
                ,keystore_password.toCharArray());
        //密钥的密码，此密码和别名要匹配
        String keypassword = "xuecheng";
        //密钥别名
        String alias = "xckey";
        //密钥对（密钥和公钥）
        KeyPair keyPair = keyStoreKeyFactory.getKeyPair(alias,keypassword.toCharArray());
        //私钥
        RSAPrivateKey aPrivate = (RSAPrivateKey) keyPair.getPrivate();
        //定义payload信息
        Map<String, Object> tokenMap = new HashMap<>();
        tokenMap.put("id", "123");
        tokenMap.put("name", "mrt");
        tokenMap.put("roles", "r01,r02");
        tokenMap.put("ext", "1");
        //生成jwt令牌
        Jwt jwt = JwtHelper.encode(JSON.toJSONString(tokenMap), new RsaSigner(aPrivate));
        //取出jwt令牌
        String token = jwt.getEncoded();
        System.out.println("token="+token);
    }
    @Test
    public void verifyTest(){
        //令牌
//       String token="eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHQiOiIxIiwicm9sZXMiOiJyMDEscjAyIiwibmFtZSI6Im1ydCIsImlkIjoiMTIzIn0.dcqgJHkgpluriE2VNWfm5ryT2752KLOhtYkDl062UVFrEFAaL4-lGi0Kujeznqwq-0HsxuDHARo_URnRH4ciItIW5rMuQECyvbIa4FCxGFlZWeNofE5BgSGhGYxGZMKfPGdypO6WZAKpfi36kzynQmTQZdHFCxWQwk6_VfsXr-clX1hLzyYt90fL3HLKyYC29ELx-39foj-gDlcOuVVZGLxcZktxS2oOBt-q4r-kBMLBf-qOACG_wOCDWoLQIG-Byy7ScL7uYGtZA0ORBq4fcWBBX7_Cq0-5ADWYtZ3PBUVOWFRJ2fPBDIQ78VbMkGDn4YBfEr10aL7Jf2HvV1mXYQ";
//        String token = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJjb21wYW55SWQiOm51bGwsInVzZXJwaWMiOm51bGwsInVzZXJfbmFtZSI6ImFkbWluIiwic2NvcGUiOlsiYXBwIl0sIm5hbWUiOiLns7vnu5_nrqHnkIblkZgiLCJ1dHlwZSI6IjEwMTAwMyIsImlkIjoiNDgiLCJleHAiOjE1NjkwOTg3MTksImF1dGhvcml0aWVzIjpbImNvdXJzZV90ZWFjaHBsYW5fbGlzdCJdLCJqdGkiOiJlNjIxMzU2My01NWU5LTRlOTUtOWI5YS0zZDQ5Y2MzNTA4ZDciLCJjbGllbnRfaWQiOiJYY1dlYkFwcCJ9.ZLz5BfbQRcES_jvZA4mxpfAV_TzNruIH7Ge8ADsZxf5o3bGjmx0TbHgVailygloa-wFJAKT1OOjMJFX29OnRFIrruxZ8MnppRHVNgGX2VUaSkCuMrL5PhBdI5qaIcqziggeIJEtvN9-j-k5VaphO6X991BiU4NtajU_ovEi-f8OuyFnyOwtFtqzWNlW972NNWEkjcLA3m4-aYx1rV6vnwa6uD06IAaiiw_UC9GQ5sfgBh9zzKW10aO50a6o3DjR5YGc_vqJd6Vmhph_7vI9R_1VqW15HIi-kotoqYr65CQ7fF_JZpw8C7s0Na5qtFCb-f9FfkmC-Aip_c-p28aODwQ";
//        String token = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJjb21wYW55SWQiOm51bGwsInVzZXJwaWMiOm51bGwsInVzZXJfbmFtZSI6ImFkbWluIiwic2NvcGUiOlsiYXBwIl0sIm5hbWUiOiLns7vnu5_nrqHnkIblkZgiLCJ1dHlwZSI6IjEwMTAwMyIsImlkIjoiNDgiLCJleHAiOjE1NjkxODExMjAsImF1dGhvcml0aWVzIjpbInhjX3N5c21hbmFnZXJfZG9jIiwieGNfc3lzbWFuYWdlcl91c2VyIiwieGNfdGVhY2htYW5hZ2VyX2NvdXJzZV9iYXNlIiwieGNfc3lzbWFuYWdlcl91c2VyX3ZpZXciLCJ4Y19zeXNtYW5hZ2VyIiwieGNfc3lzbWFuYWdlcl9sb2ciLCJ4Y19zeXNtYW5hZ2VyX3VzZXJfZWRpdCIsInhjX3RlYWNobWFuYWdlcl9jb3Vyc2UiLCJ4Y19zeXNtYW5hZ2VyX3VzZXJfYWRkIiwieGNfc3lzbWFuYWdlcl91c2VyX2RlbGV0ZSIsInhjX3RlYWNobWFuYWdlcl9jb3Vyc2VfYWRkIl0sImp0aSI6ImVlNWNjNjE4LTMxY2QtNDZkNC1iNTU2LTRmZjljYzFmOTU1YSIsImNsaWVudF9pZCI6IlhjV2ViQXBwIn0.MvJkO6SoGgE6b2OHib0slBv6Z0q1rwq1b3SZKjU8tfYWi--uidoij9afGOBOL2ckZ_Fp1Rm4iE6a2WG_J4xKy84aYtS2pMg9qHp7Fw3nfpi5Bv0pKgq-U4Ya-tQIhR9py9X6O5fdUeX1lX7vs4QGAY-cfTL8RLPDf15ahlZhTwprwwqmFpsoaDHj2VqHy4CCl58AW0NlCVXzRCwclPZJs4KYKZ0va4NFLad-rTFJvPHSBiTtjdX_AcpYSI3VSwuDKDCFaATq5uwRoDbfV91O9hEgpEd_yynZmf_cK3Qbk785TW-JLD6twei8nMSFmUmIJHAOziOCVcnRdAUO5pYvvQ";
        String token = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJjb21wYW55SWQiOm51bGwsInVzZXJwaWMiOm51bGwsInVzZXJfbmFtZSI6ImFkbWluIiwic2NvcGUiOlsiYXBwIl0sIm5hbWUiOiLns7vnu5_nrqHnkIblkZgiLCJ1dHlwZSI6IjEwMTAwMyIsImlkIjoiNDgiLCJleHAiOjE1NjkxODI1MzksImF1dGhvcml0aWVzIjpbInhjX3N5c21hbmFnZXJfZG9jIiwieGNfc3lzbWFuYWdlcl91c2VyIiwieGNfdGVhY2htYW5hZ2VyX2NvdXJzZV9iYXNlIiwieGNfc3lzbWFuYWdlcl91c2VyX3ZpZXciLCJ4Y19zeXNtYW5hZ2VyIiwieGNfc3lzbWFuYWdlcl9sb2ciLCJ4Y19zeXNtYW5hZ2VyX3VzZXJfZWRpdCIsInhjX3RlYWNobWFuYWdlcl9jb3Vyc2UiLCJ4Y19zeXNtYW5hZ2VyX3VzZXJfYWRkIiwieGNfc3lzbWFuYWdlcl91c2VyX2RlbGV0ZSIsInhjX3RlYWNobWFuYWdlcl9jb3Vyc2VfYWRkIl0sImp0aSI6IjZmYzQ1MjJlLWQxMTgtNDFhZC04NWNlLWJiMGJmODU3MDU5MyIsImNsaWVudF9pZCI6IlhjV2ViQXBwIn0.K5WIBzrhsng8IS4UUKCI-CMe3eUuX45hZWn9azNz9vVJIQifVe_U-txL-Cdd1PhCJc4My8viBwPd5cfH8_6V3EhlW1GDqtloB2YT4kqBm0WT9j6gpFPwIKxrg8dpzPx2YXjJPvIQtGORJn5zuGo47kHREt1zjnGqAS4K861hDPfUvJMy-8gj1vkIgscKXhy1z7SU3nuBkCzb0e0F4bFZd_s14CYYIdDCVKyFPpZMh93K0GwUjKaEtqLrdZT-S5tqgbd74wWKuFuHeQiFW5HCMlk82jmXlAUJEmL4xLhxRCNeP9d4IIIALlfx6Spq7L8JVropoXZH9XrPUif3Y1pSiA";
        //公钥
//       String publicKey = "-----BEGIN PUBLIC KEY-----MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAhj8ijpyU2FeuH8m4AEPb8/kNCnxE8qCnLOqnu1TIbs0jUdROvKysoamI985kbWxQg3WRXZT3bH81RdWYw/EeiKXPaEdzkqYJCAsGS7cpjqmYPGeOOSG4K4vgFH3+36SgjHjs6KYXNgTuFrORLjcFYESNFcFmYhYt3Qzc228c6MTxVMJCi0s3Kd31TrZrlUwnoqvvPE7ZVC5SWcIucQNL4KpzUfwVkXfoCR25w3uYwX6GmVF7e6wpsC1jYF9CJzqek7UYgepv7Q0JNmBZ1SiTeeZSE2bKccGdUo5MtZrHZp99Qon/3HaYUa0V9/vTfP3FqU8lhKSW0xD1QGPI8RUXcQIDAQAB-----END PUBLIC KEY-----";
        String publicKey = "-----BEGIN PUBLIC KEY-----MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAnASXh9oSvLRLxk901HANYM6KcYMzX8vFPnH/To2R+SrUVw1O9rEX6m1+rIaMzrEKPm12qPjVq3HMXDbRdUaJEXsB7NgGrAhepYAdJnYMizdltLdGsbfyjITUCOvzZ/QgM1M4INPMD+Ce859xse06jnOkCUzinZmasxrmgNV3Db1GtpyHIiGVUY0lSO1Frr9m5dpemylaT0BV3UwTQWVW9ljm6yR3dBncOdDENumT5tGbaDVyClV0FEB1XdSKd7VjiDCDbUAUbDTG1fm3K9sx7kO1uMGElbXLgMfboJ963HEJcU01km7BmFntqI5liyKheX+HBUCD4zbYNPw236U+7QIDAQAB-----END PUBLIC KEY-----";
        //校验jwt
        Jwt jwt = JwtHelper.decodeAndVerify(token, new RsaVerifier(publicKey));
        //获取jwt原始内容
        String claims = jwt.getClaims();
        System.out.println(claims);
        //jwt令牌
        String encoded = jwt.getEncoded();

    }
    
}
