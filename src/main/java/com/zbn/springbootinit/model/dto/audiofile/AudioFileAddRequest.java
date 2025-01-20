package com.zbn.springbootinit.model.dto.audiofile;

import lombok.Data;

@Data
public class AudioFileAddRequest {
    /**
     * 音频文件原始文件名
     */
    private String fileName;

    /**
     * 音频文件存储路径
     */
    private String filePath;

    /**
     * 音频文件大小（字节）
     */
    private Long fileSize;

    /**
     * 音频文件MIME类型（如audio/mp3）
     */
    private String fileType;

    /**
     * 音频时长（秒）
     */
    private Integer duration;

    /**
     * 上传文件的用户ID（可选）
     */
    private Long userId;

    /**
     * 音频标题（可选）
     */
    private String title;

    /**
     * 音频描述（可选）
     */
    private String description;
    /**
     * 图片（可选）
     */
    private String  picture;
}
