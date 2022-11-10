package com.pianotranscriptioncli.service.impl;

import com.pianotranscriptioncli.common.api.CommonResult;
import com.pianotranscriptioncli.dto.Mp3ImportDTO;
import com.pianotranscriptioncli.dto.Mp3ImportWithFileDTO;
import com.pianotranscriptioncli.service.TranscriptionService;
import com.pianotranscriptioncli.utils.Utils;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

@Service
public class TranscriptionServiceImpl implements TranscriptionService {

    @Override
    public CommonResult Mp3TOMidiUpload(Mp3ImportDTO mp3ImportDTO) throws Exception {
        String resourcePath;
        if (mp3ImportDTO.isAbsolute()) {
            resourcePath = mp3ImportDTO.getResourcePath();
        } else {
            System.out.println(System.getProperty("user.dir"));
            resourcePath = System.getProperty("user.dir") + mp3ImportDTO.getResourcePath(); // "\\src\\main\\resources\\"
        }
        String outPath = mp3ImportDTO.getOutPath() + mp3ImportDTO.getSongName() + ".mid";
        String ans = Utils.ConvertorRedirect(resourcePath, mp3ImportDTO.getSongName(), outPath);
        if (ans != null) {
            return CommonResult.success(ans, "mp3转换成功");
        } else {
            return CommonResult.failed("mp3转换失败");
        }
    }

    /**
     * 上传文件
     * @param mp3ImportWithFileDTO
     * @param response
     * @return CommonResult
     * @throws Exception
     */
    @Override
    public CommonResult Mp3TOMidiUploadWithFile(Mp3ImportWithFileDTO mp3ImportWithFileDTO, HttpServletResponse response) throws Exception {
        MultipartFile file = mp3ImportWithFileDTO.getFile();
        String inputFilePath;
        if (!file.isEmpty()) {
        /*
            String fileName = file.getOriginalFilename(); // 获取文件名
            assert fileName != null;
            String suffixName = fileName.substring(fileName.lastIndexOf(".")); // 获取文件的后缀名
        */
            inputFilePath = mp3ImportWithFileDTO.getInputPath() + "input\\" + mp3ImportWithFileDTO.getSongName() + ".mp3";
            File dest = new File(inputFilePath);
            if (!dest.getParentFile().exists()) { // 检测是否存在目录
                dest.getParentFile().mkdirs();
            }
            try {
                file.transferTo(dest);
                System.out.println("mp3文件存入成功!");
            } catch (IllegalStateException e) {
                e.printStackTrace();
                return CommonResult.failed("mp3文件存入失败!" + e.getMessage());
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("mp3文件上传失败!" + e);
                return CommonResult.failed("mp3文件存入失败!" + e.getMessage());
            }
        } else {
            System.out.println("无mp3文件！");
            return CommonResult.failed("失败！无mp3文件！");
        }

        String outPath = mp3ImportWithFileDTO.getOutPath() + mp3ImportWithFileDTO.getSongName() + ".mid";
        String output = Utils.ConvertorRedirect(mp3ImportWithFileDTO.getInputPath(), mp3ImportWithFileDTO.getSongName(), outPath);
        OutputStream outputStream = null;
        if (output != null) {
            File outputFile = new File(output);
            if (!outputFile.exists()) {
                throw new Exception("midi文件不存在");
            }
            // System.out.println(outputFile);
            try {
                // 通过response返回
                // 设置文件头 (URLEncoder.encode(mp3ImportWithFileDTO.getSongName() + ".mid", StandardCharsets.US_ASCII)))
                response.setHeader("Content-Disposition", "attchement;filename=" + URLEncoder.encode(mp3ImportWithFileDTO.getSongName() + ".mid", StandardCharsets.UTF_8));
                response.setCharacterEncoding("UTF-8");
                response.setContentType("audio/mid");
                // response.setContentType("application/octet-stream");
                InputStream fis = new BufferedInputStream(new FileInputStream(outputFile));
                byte[] buffer = new byte[fis.available()];
                // fis.read(buffer);
                fis.close();
                response.reset();
                outputStream = new BufferedOutputStream(response.getOutputStream());
                System.out.println(Arrays.toString(buffer));
                outputStream.write(buffer);
                System.out.println(outputStream);
                outputStream.flush();
                // IOUtils.copy(fis, outputStream);
                response.flushBuffer();
            } catch (Exception e) {
                if (null != outputStream) {
                    try {
                        outputStream.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
            System.out.println(response.getOutputStream());
            return CommonResult.success(response.getClass(), "mp3转换成功");
        } else {
            return CommonResult.failed("mp3转换失败");
        }
    }

    @Override
    public String WavToMidiUpload() {
        return "null";
    }
}


