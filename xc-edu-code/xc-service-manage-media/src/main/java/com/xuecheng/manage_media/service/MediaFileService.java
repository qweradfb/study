package com.xuecheng.manage_media.service;

import com.xuecheng.framework.domain.media.MediaFile;
import com.xuecheng.framework.domain.media.request.QueryMediaFileRequest;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.manage_media.dao.MediaFileRepository;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

@Service
public class MediaFileService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MediaFileService.class);

    @Autowired
    private MediaFileRepository mediaFileRepository;

    public QueryResponseResult findList(int page, int size, QueryMediaFileRequest queryMediaFileRequest) {
        MediaFile mediaFile = new MediaFile();
        String fileOriginalName = queryMediaFileRequest.getFileOriginalName();
        if (StringUtils.isNotEmpty(fileOriginalName)) {
            mediaFile.setFileOriginalName(fileOriginalName);
        }
        String processStatus = queryMediaFileRequest.getProcessStatus();
        if (StringUtils.isNotEmpty(processStatus)) {
            mediaFile.setProcessStatus(processStatus);
        }
        String tag = queryMediaFileRequest.getTag();
        if (StringUtils.isNotEmpty(tag)) {
            mediaFile.setTag(tag);
        }

        ExampleMatcher em = ExampleMatcher.matching()
                .withMatcher("fileOriginalName",ExampleMatcher.GenericPropertyMatchers.contains())
                .withMatcher("tag",ExampleMatcher.GenericPropertyMatchers.contains());
        Example example = Example.of(mediaFile,em);
        if (page<0) page = 1;
        page = page-1;
        if (size<0) size = 10;
        Pageable pageable = new PageRequest(page,size);
        Page all = mediaFileRepository.findAll(example, pageable);
        QueryResult queryResult = new QueryResult();
        queryResult.setList(all.getContent());
        queryResult.setTotal(all.getTotalElements());
        QueryResponseResult result = new QueryResponseResult(CommonCode.SUCCESS,queryResult);
        return result;
    }
}
