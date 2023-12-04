package com.ht.activiti.test;

import com.sun.xml.internal.ws.api.pipe.Engine;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Test;

/**
 * @Description: TODO
 * @Author: ht
 * @Date: 2023/12/4
 */
public class Test02 {

    private static final String EVECTION = "evection";

    /**
     * @Description: 启动流程实例
     * @Date: 2023/12/4
     */
    @Test
    public void test01(){
        // 1.获取ProcessEngine对象-流程引擎
        ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
        // 2.获取runtimeService对象
        RuntimeService runtimeService = engine.getRuntimeService();
        // 3.启动流程实例(参数：1.processDefinitionKey  2.businessKey)
        ProcessInstance instance = runtimeService.startProcessInstanceByKey("evection", "1001");
        // 4.输出processInstance相关属性
        System.out.println("businessKey = "+instance.getBusinessKey());
    }

    /**
     * @Description: 全部流程实例挂起与激活
     * @Date: 2023/12/4
     */
    @Test
    public void test02(){
        // 1.获取ProcessEngine对象
        ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
        // 2.获取RepositoryService对象
        RepositoryService repositoryService = engine.getRepositoryService();
        // 3.查询流程定义的对象
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey("evection")
                .singleResult();
        // 4.获取当前流程定义的状态
        boolean suspended = processDefinition.isSuspended();
        String id = processDefinition.getId();
        // 5.如果挂起就激活
        if(suspended){
            // 表示当前定义的流程状态是 挂起的
            repositoryService.activateProcessDefinitionById(
                    id, //流程定义的id
                    true, // 是否激活
                    null  // 激活时间
            );
            System.out.println("流程定义:"+id+",已激活");
        }else{
            // 非挂起状态,激活状态,那么需要挂起流程定义
            repositoryService.suspendProcessDefinitionById(
                    id,   // 流程id
                    true,   //是否挂起
                    null   // 挂起时间
            );
            System.out.println("流程定义："+id+",已挂起");
        }
    }

    /**
     * @Description: 单个流程实例挂起与激活
     * @Date: 2023/12/4
     */
    @Test
    public void test03(){
        // 1.获取ProcessEngine对象
        ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
        // 2.获取RuntimeService
        RuntimeService runtimeService = engine.getRuntimeService();
        // 3.获取流程实例对象
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                .processInstanceId("10001")
                .singleResult();

        // 4.获取相关的状态操作
        boolean suspended = processInstance.isSuspended();
        String id = processInstance.getId();
        if(suspended){
            // 挂起--》激活
            runtimeService.activateProcessInstanceById(id);
            System.out.println("流程定义：" + id + "，已激活");
        }else{
            // 激活--》挂起
            runtimeService.suspendProcessInstanceById(id);
            System.out.println("流程定义：" + id + "，已挂起");
        }
    }

    /**
     * @Description: 流程任务处理
     * @Date: 2023/12/4
     */
    @Test
    public void test04() {
        ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
        TaskService taskService = engine.getTaskService();
        Task task = taskService.createTaskQuery().processDefinitionKey(EVECTION).taskAssignee("wangwu").singleResult();
        // 完成任务
        taskService.complete(task.getId());
    }


}
