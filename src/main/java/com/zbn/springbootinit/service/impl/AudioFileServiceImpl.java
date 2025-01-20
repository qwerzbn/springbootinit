package com.zbn.springbootinit.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zbn.springbootinit.common.ErrorCode;
import com.zbn.springbootinit.constant.CommonConstant;
import com.zbn.springbootinit.exception.ThrowUtils;
import com.zbn.springbootinit.mapper.AudioFileMapper;
import com.zbn.springbootinit.model.dto.audiofile.AudioFileQueryRequest;
import com.zbn.springbootinit.model.entity.AudioFile;
import com.zbn.springbootinit.model.entity.User;
import com.zbn.springbootinit.model.vo.AudioFileVO;
import com.zbn.springbootinit.model.vo.UserVO;
import com.zbn.springbootinit.service.AudioFileService;
import com.zbn.springbootinit.service.UserService;
import com.zbn.springbootinit.utils.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 音频文件服务实现
 *
 * @author <a href="https://github.com/qwerzbn">zbn</a>
 */
@Service
@Slf4j
public class AudioFileServiceImpl extends ServiceImpl<AudioFileMapper, AudioFile> implements AudioFileService {

    @Resource
    private UserService userService;

    /**
     * 校验数据
     *
     * @param audioFile
     * @param add       对创建的数据进行校验
     */
    @Override
    public void validAudioFile(AudioFile audioFile, boolean add) {
        ThrowUtils.throwIf(audioFile == null, ErrorCode.PARAMS_ERROR);
        // todo 从对象中取值
        String title = audioFile.getTitle();
        // 创建数据时，参数不能为空
        if (add) {
            // todo 补充校验规则
            ThrowUtils.throwIf(StringUtils.isBlank(title), ErrorCode.PARAMS_ERROR);
        }
        // 修改数据时，有参数则校验
        // todo 补充校验规则
        if (StringUtils.isNotBlank(title)) {
            ThrowUtils.throwIf(title.length() > 80, ErrorCode.PARAMS_ERROR, "标题过长");
        }
    }

    /**
     * 获取查询条件
     *
     * @param audioFileQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<AudioFile> getQueryWrapper(AudioFileQueryRequest audioFileQueryRequest) {
        QueryWrapper<AudioFile> queryWrapper = new QueryWrapper<>();
        if (audioFileQueryRequest == null) {
            return queryWrapper;
        }
        //  从对象中取值
        Long id = audioFileQueryRequest.getId();
        Long userId = audioFileQueryRequest.getUserId();
        String title = audioFileQueryRequest.getTitle();
        String description = audioFileQueryRequest.getDescription();
        String sortField = audioFileQueryRequest.getSortField();
        String sortOrder = audioFileQueryRequest.getSortOrder();
        String searchText = audioFileQueryRequest.getSearchText();

        // 补充需要的查询条件
        // 从多字段中搜索
        if (StringUtils.isNotBlank(searchText)) {
            // 需要拼接查询条件
            queryWrapper.and(qw -> qw.like("title", searchText).or().like("description", searchText));
        }
        // 模糊查询
        queryWrapper.like(StringUtils.isNotBlank(title), "title", title);
        queryWrapper.like(StringUtils.isNotBlank(description), "description", description);
        // 精确查询
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        // 排序规则
        queryWrapper.orderBy(SqlUtils.validSortField(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    /**
     * 获取音频文件封装
     *
     * @param audioFile
     * @param request
     * @return
     */
    @Override
    public AudioFileVO getAudioFileVO(AudioFile audioFile, HttpServletRequest request) {
        // 对象转封装类
        AudioFileVO audioFileVO = AudioFileVO.objToVo(audioFile);
        // region 可选
        // 1. 关联查询用户信息
        Long userId = audioFile.getUserId();
        User user = null;
        if (userId != null && userId > 0) {
            user = userService.getById(userId);
        }
        UserVO userVO = userService.getUserVO(user);
        audioFileVO.setUserVO(userVO);
        // endregion

        return audioFileVO;
    }

    /**
     * 分页获取音频文件封装
     *
     * @param audioFilePage
     * @param request
     * @return
     */
    @Override
    public Page<AudioFileVO> getAudioFileVOPage(Page<AudioFile> audioFilePage, HttpServletRequest request) {
        List<AudioFile> audioFileList = audioFilePage.getRecords();
        Page<AudioFileVO> audioFileVOPage = new Page<>(audioFilePage.getCurrent(), audioFilePage.getSize(), audioFilePage.getTotal());
        if (CollUtil.isEmpty(audioFileList)) {
            return audioFileVOPage;
        }
        // 对象列表 => 封装对象列表
        List<AudioFileVO> audioFileVOList = audioFileList.stream().map(audioFile -> {
            return AudioFileVO.objToVo(audioFile);
        }).collect(Collectors.toList());

        // region 可选
        // 1. 关联查询用户信息
        Set<Long> userIdSet = audioFileList.stream().map(AudioFile::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));
        // 2，填充信息
        audioFileVOList.forEach(audioFileVO -> {
            Long userId = audioFileVO.getUserId();
            User user = new User();
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            audioFileVO.setUserVO(userService.getUserVO(user));
        });
        // endregion

        audioFileVOPage.setRecords(audioFileVOList);
        return audioFileVOPage;
    }

}
