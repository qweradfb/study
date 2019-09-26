package com.xuecheng.order.service;

import com.xuecheng.framework.domain.task.XcTask;
import com.xuecheng.framework.domain.task.XcTaskHis;
import com.xuecheng.order.dao.XcTaskHisRepository;
import com.xuecheng.order.dao.XcTaskRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    @Autowired
    private XcTaskRepository xcTaskRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private XcTaskHisRepository xcTaskHisRepository;

    //取出前n条任务,取出指定时间之前处理的任务
   public List<XcTask> findTaskList(Date upDateTime, int n){
       Pageable pageable = new PageRequest(0,n);
       Page<XcTask> byUpdateTimeBefore = xcTaskRepository.findByUpdateTimeBefore(pageable, upDateTime);
       List<XcTask> content = byUpdateTimeBefore.getContent();
       return content;
   }

    /**
     * //发送消息
     * @param xcTask 任务对象
     */
    @Transactional
    public void publish(XcTask xcTask){
        String taskId = xcTask.getId();
        //查询任务
        Optional<XcTask> taskOptional = xcTaskRepository.findById(taskId);
        if(taskOptional.isPresent()){
            XcTask one = taskOptional.get();
        //String exchange, String routingKey, Object object
            String ex = one.getMqExchange();
            String routingKey = one.getMqRoutingkey();
            rabbitTemplate.convertAndSend(ex,routingKey,xcTask);
            //更新任务时间为当前时间
            xcTask.setUpdateTime(new Date());
            xcTask.setVersion(xcTask.getVersion()+1);
            xcTaskRepository.save(xcTask);
        }
    }
    public boolean estUpdate(String id,int oldVersion){
        List<XcTask> byIdAndVersion = xcTaskRepository.findByIdAndVersion(id, oldVersion);

        return byIdAndVersion.size()>0;
    }

    //删除任务
    //删除任务
    @Transactional
    public void finishTask(String taskId){
        Optional<XcTask> taskOptional = xcTaskRepository.findById(taskId);
        if(taskOptional.isPresent()){
            XcTask xcTask = taskOptional.get();
            xcTask.setDeleteTime(new Date());
            XcTaskHis xcTaskHis = new XcTaskHis();
            BeanUtils.copyProperties(xcTask, xcTaskHis);
            xcTaskHisRepository.save(xcTaskHis);
            xcTaskRepository.delete(xcTask);
        }
    }
}
