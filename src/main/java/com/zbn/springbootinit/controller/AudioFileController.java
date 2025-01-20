package com.zbn.springbootinit.controller;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zbn.springbootinit.annotation.AuthCheck;
import com.zbn.springbootinit.common.BaseResponse;
import com.zbn.springbootinit.common.DeleteRequest;
import com.zbn.springbootinit.common.ErrorCode;
import com.zbn.springbootinit.common.ResultUtils;
import com.zbn.springbootinit.constant.UserConstant;
import com.zbn.springbootinit.exception.BusinessException;
import com.zbn.springbootinit.exception.ThrowUtils;
import com.zbn.springbootinit.model.dto.audiofile.AudioFileAddRequest;
import com.zbn.springbootinit.model.dto.audiofile.AudioFileQueryRequest;
import com.zbn.springbootinit.model.dto.audiofile.AudioFileUpdateRequest;
import com.zbn.springbootinit.model.entity.AudioFile;
import com.zbn.springbootinit.model.entity.User;
import com.zbn.springbootinit.model.vo.AudioFileVO;
import com.zbn.springbootinit.service.AudioFileService;
import com.zbn.springbootinit.service.UserService;
import io.github.classgraph.json.JSONUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 音频文件接口
 *
 * @author <a href="https://github.com/qwerzbn">zbn</a>
 */
@RestController
@RequestMapping("/audioFile")
@Slf4j
public class AudioFileController {

    @Resource
    private AudioFileService audioFileService;

    @Resource
    private UserService userService;

    // region 增删改查

    /**
     * 创建音频文件
     *
     * @param audioFileAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addAudioFile(@RequestBody AudioFileAddRequest audioFileAddRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(audioFileAddRequest == null, ErrorCode.PARAMS_ERROR);
        AudioFile audioFile = new AudioFile();
        BeanUtils.copyProperties(audioFileAddRequest, audioFile);
        audioFile.setTags(JSONUtil.toJsonStr(audioFileAddRequest.getTags()));
        // 数据校验
        audioFileService.validAudioFile(audioFile, true);
        // 填充默认值
        User loginUser = userService.getLoginUser(request);
        audioFile.setUserId(loginUser.getId());
        // 写入数据库
        boolean result = audioFileService.save(audioFile);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        // 返回新写入的数据 id
        long newAudioFileId = audioFile.getId();
        return ResultUtils.success(newAudioFileId);
    }

    /**
     * 删除音频文件
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteAudioFile(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        AudioFile oldAudioFile = audioFileService.getById(id);
        ThrowUtils.throwIf(oldAudioFile == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldAudioFile.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 操作数据库
        boolean result = audioFileService.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 更新音频文件（仅管理员可用）
     *
     * @param audioFileUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateAudioFile(@RequestBody AudioFileUpdateRequest audioFileUpdateRequest) {
        if (audioFileUpdateRequest == null || audioFileUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 在此处将实体类和 DTO 进行转换
        AudioFile audioFile = new AudioFile();
        BeanUtils.copyProperties(audioFileUpdateRequest, audioFile);
        audioFile.setTags(JSONUtil.toJsonStr(audioFileUpdateRequest.getTags()));
        // 数据校验
        audioFileService.validAudioFile(audioFile, false);
        // 判断是否存在
        long id = audioFileUpdateRequest.getId();
        AudioFile oldAudioFile = audioFileService.getById(id);
        ThrowUtils.throwIf(oldAudioFile == null, ErrorCode.NOT_FOUND_ERROR);
        // 操作数据库
        boolean result = audioFileService.updateById(audioFile);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 根据 id 获取音频文件（封装类）
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<AudioFileVO> getAudioFileVOById(long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        AudioFile audioFile = audioFileService.getById(id);
        ThrowUtils.throwIf(audioFile == null, ErrorCode.NOT_FOUND_ERROR);
        // 获取封装类
        return ResultUtils.success(audioFileService.getAudioFileVO(audioFile, request));
    }

    /**
     * 分页获取音频文件列表（仅管理员可用）
     *
     * @param audioFileQueryRequest
     * @return
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<AudioFile>> listAudioFileByPage(@RequestBody AudioFileQueryRequest audioFileQueryRequest) {
        long current = audioFileQueryRequest.getCurrent();
        long size = audioFileQueryRequest.getPageSize();
        // 查询数据库
        Page<AudioFile> audioFilePage = audioFileService.page(new Page<>(current, size),
                audioFileService.getQueryWrapper(audioFileQueryRequest));
        return ResultUtils.success(audioFilePage);
    }

    /**
     * 分页获取音频文件列表（封装类）
     *
     * @param audioFileQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<AudioFileVO>> listAudioFileVOByPage(@RequestBody AudioFileQueryRequest audioFileQueryRequest,
                                                                 HttpServletRequest request) {
        long current = audioFileQueryRequest.getCurrent();
        long size = audioFileQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Page<AudioFile> audioFilePage = audioFileService.page(new Page<>(current, size),
                audioFileService.getQueryWrapper(audioFileQueryRequest));
        // 获取封装类
        return ResultUtils.success(audioFileService.getAudioFileVOPage(audioFilePage, request));
    }

    /**
     * 分页获取当前登录用户创建的音频文件列表
     *
     * @param audioFileQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/my/list/page/vo")
    public BaseResponse<Page<AudioFileVO>> listMyAudioFileVOByPage(@RequestBody AudioFileQueryRequest audioFileQueryRequest,
                                                                   HttpServletRequest request) {
        ThrowUtils.throwIf(audioFileQueryRequest == null, ErrorCode.PARAMS_ERROR);
        // 补充查询条件，只查询当前登录用户的数据
        User loginUser = userService.getLoginUser(request);
        audioFileQueryRequest.setUserId(loginUser.getId());
        long current = audioFileQueryRequest.getCurrent();
        long size = audioFileQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Page<AudioFile> audioFilePage = audioFileService.page(new Page<>(current, size),
                audioFileService.getQueryWrapper(audioFileQueryRequest));
        // 获取封装类
        return ResultUtils.success(audioFileService.getAudioFileVOPage(audioFilePage, request));
    }
    // endregion
}
