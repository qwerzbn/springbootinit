package com.zbn.springbootinit.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 音频文件存储表
 *
 * @TableName audio_file
 */
@TableName(value = "audio_file")
@Data
public class AudioFile {
    /**
     * 音频文件唯一ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

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
    private String picture;
    /**
     * 标签
     */
    private String tags;
    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 是否删除
     */
    @TableLogic
    private Integer isDelete;
}