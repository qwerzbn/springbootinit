package com.zbn.springbootinit.model.vo;

import cn.hutool.json.JSONUtil;
import com.zbn.springbootinit.model.entity.AudioFile;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.Date;
import java.util.List;

@Data
public class AudioFileVO {
    /**
     * 音频文件唯一ID
     */
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
     * 图片路径
     */
    private String picture;
    /**
     * 标签
     */
    private List<String> tags;
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
    private Integer isDelete;

    /**
     * 用户信息
     */
    private UserVO userVO;

    public static AudioFileVO objToVo(AudioFile audioFile) {
        if (audioFile == null) return null;
        AudioFileVO audioFileVO = new AudioFileVO();
        BeanUtils.copyProperties(audioFile, audioFileVO);
        if (audioFile.getTags() != null) {
            audioFileVO.setTags(JSONUtil.toList(audioFile.getTags(), String.class));
        }
        return audioFileVO;
    }

    public AudioFile voToObj(AudioFileVO audioFileVO) {
        if (audioFileVO == null) return null;
        AudioFile audioFile = new AudioFile();
        BeanUtils.copyProperties(audioFileVO, audioFile);
        if (audioFileVO.getTags() != null) {
            audioFile.setTags(JSONUtil.toJsonStr(audioFileVO.getTags()));
        }
        return audioFile;
    }
}
