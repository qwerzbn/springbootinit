package com.zbn.springbootinit.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zbn.springbootinit.model.dto.audiofile.AudioFileQueryRequest;
import com.zbn.springbootinit.model.entity.AudioFile;
import com.zbn.springbootinit.model.vo.AudioFileVO;

import javax.servlet.http.HttpServletRequest;

/**
 * 音频文件服务
 *
 * @author <a href="https://github.com/qwerzbn">zbn</a>
 */
public interface AudioFileService extends IService<AudioFile> {

    /**
     * 校验数据
     *
     * @param audioFile
     * @param add       对创建的数据进行校验
     */
    void validAudioFile(AudioFile audioFile, boolean add);

    /**
     * 获取查询条件
     *
     * @param audioFileQueryRequest
     * @return
     */
    QueryWrapper<AudioFile> getQueryWrapper(AudioFileQueryRequest audioFileQueryRequest);

    /**
     * 获取音频文件封装
     *
     * @param audioFile
     * @param request
     * @return
     */
    AudioFileVO getAudioFileVO(AudioFile audioFile, HttpServletRequest request);

    /**
     * 分页获取音频文件封装
     *
     * @param audioFilePage
     * @param request
     * @return
     */
    Page<AudioFileVO> getAudioFileVOPage(Page<AudioFile> audioFilePage, HttpServletRequest request);
}
