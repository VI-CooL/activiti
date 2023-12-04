package com.ht.activiti.test;

import org.activiti.engine.HistoryService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricActivityInstanceQuery;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.zip.ZipInputStream;

/**
 * @Description:
 * @Author: ht
 * @Date: 2023/11/28
 */
public class Test01 {
    private static final String EVECTION = "evection";
    private static final String Assignee = "zhangsan";

    /**
     * @Description: 在Mysql生成Activiti相关的表结构
     * @Date: 2023/11/28
     */
    @Test
    public void test01() {
        // 使用classpath下的activiti.cfg.xml中的配置来创建 ProcessEngine对象
        // 初始化加载默认的activiti.cfg.xml文件
        // ProcessEngine-流程引擎
        ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
        System.out.println(engine);
    }

    /**
     * @Description: 自定义的方式来加载配置文件
     * @Date: 2023/11/29
     */
    @Test
    public void test02() {
        // 首先创建ProcessEngineConfiguration对象
        ProcessEngineConfiguration configuration = ProcessEngineConfiguration.createProcessEngineConfigurationFromResource("activiti.cfg.xml");
        // 通过ProcessEngineConfiguration对象来创建ProcessEngine
        ProcessEngine processEngine = configuration.buildProcessEngine();
    }

    /**
     * @Description: 实现文件的单个部署
     * @Date: 2023/11/29
     */
    @Test
    public void test03() {
        // 1.获取ProcessEngine对象
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        // 2.获取Repository进行部署操作
        RepositoryService repositoryService = processEngine.getRepositoryService();
        // 3.使用repositoryService进行部署操作
        Deployment deploy = repositoryService.createDeployment().addClasspathResource("bpmn/evection.bpmn") // 添加bpmn资源
                .addClasspathResource("bpmn/evection.png")  // 添加png资源
                .name("出差申请单").deploy();// 部署流程
        // 4.输出流程部署的信息
        System.out.println("流程部署的id：" + deploy.getId());
        System.out.println("流程部署的名称：" + deploy.getName());
    }

    /**
     * @Description: 通过一个zip文件来部署操作
     * @Date: 2023/11/29
     */
    @Test
    public void test04() {
        // 定义zip的输入流
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("bpmn/evection.zip");
        // 对inputStream做修饰
        ZipInputStream zipInputStream = new ZipInputStream(inputStream);
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        RepositoryService repositoryService = processEngine.getRepositoryService();
        Deployment deploy = repositoryService.createDeployment().addZipInputStream(zipInputStream).name("出差申请单").deploy();
        // 输出流程部署的信息
        System.out.println("流程部署的id：" + deploy.getId());
        System.out.println("流程部署的名称：" + deploy.getName());
    }

    /**
     * @Description: 启动一个流程实例
     * @Date: 2023/11/29
     */
    @Test
    public void test05() {
        // 1.创建ProcessEngine对象
        ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
        // 2.获取RuntimeService对象
        RuntimeService runtimeService = engine.getRuntimeService();
        // 3.根据流程定义的id启动流程
        String id = "evection";
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(EVECTION);
        // 4.输出相关的流程实例信息
        System.out.println("流程定义的ID:" + processInstance.getProcessDefinitionId());
        System.out.println("流程实例的ID:" + processInstance.getId());
        System.out.println("当前活动的ID:" + processInstance.getActivityId());
    }

    /**
     * @Description: 任务查询
     * @Date: 2023/11/29
     */
    @Test
    public void test06() {
        ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
        // 任务查看需要获取一个 TaskService 对象
        TaskService taskService = engine.getTaskService();
        // 根据流程的key和任务责任人  查询任务
        String assignee = "lisi";
        List<Task> list = taskService.createTaskQuery().processDefinitionKey("evection").taskAssignee(assignee).list();
        // 输出当前用户具有的任务
        list.stream().forEach(task -> {
            System.out.println("流程实例id:" + task.getProcessDefinitionId());
            System.out.println("任务id:" + task.getId());
            System.out.println("任务负责人:" + task.getAssignee());
            System.out.println("任务名称:" + task.getName());
        });
    }

    /**
     * @Description: 流程任务处理
     * @Date: 2023/11/29
     */
    @Test
    public void test07() {
        ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
        TaskService taskService = engine.getTaskService();
        Task task = taskService.createTaskQuery().processDefinitionKey(EVECTION).taskAssignee("lisi").singleResult();
        // 完成任务
        taskService.complete(task.getId());
    }

    /**
     * @Description: 流程定义的查询
     * @Date: 2023/11/29
     */
    @Test
    public void test08() {
        ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
        RepositoryService repositoryService = engine.getRepositoryService();
        // 获取一个 ProcessDefinitionQuery 对象 用来查询操作
        ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery();
        // 查询的结果信息
        List<ProcessDefinition> list = processDefinitionQuery.processDefinitionKey("evection").orderByProcessDefinitionVersion().desc().list();

        list.stream().forEach(processDefinition -> {
            System.out.println("流程定义的id：" + processDefinition.getId());
            System.out.println("流程定义的name:" + processDefinition.getName());
            System.out.println("流程定义的key:" + processDefinition.getKey());
            System.out.println("流程定义的version:" + processDefinition.getVersion());
            System.out.println("流程部署的id:" + processDefinition.getDeploymentId());
        });
    }

    /**
     * @Description: 删除流程
     * @Date: 2023/12/1
     */
    @Test
    public void test09() {
        ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
        RepositoryService repositoryService = engine.getRepositoryService();
        // 删除流程定义 如果该流程定义 已经有了流程实例,启动则删除时报错
        repositoryService.deleteDeployment("1");
        // 设置为TRUE级联删除流程定义，即使流程有实例启动，也可以删除，设置为false非级联删除操作。
        repositoryService.deleteDeployment("1", true);
    }

    /**
     * @Description: 读取数据库中的资源文件
     * @Date: 2023/12/1
     */
    @Test
    public void test10() throws IOException {
        // 1.得到ProcessEngine对象
        ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
        // 2.获取RepositoryService对象
        RepositoryService repositoryService = engine.getRepositoryService();
        // 3.得到查询器
        ProcessDefinition definition = repositoryService.createProcessDefinitionQuery().processDefinitionKey("evection").singleResult();
        // 4.获取流程部署的id
        String deploymentId = definition.getDeploymentId();
        // 5.通过RepositoryService对象来获取图片信息和bpmn信息
        // png
        InputStream pngInput = repositoryService.getResourceAsStream(deploymentId, definition.getDiagramResourceName());
        // bpmn 文件的流
        InputStream bpmnInput = repositoryService.getResourceAsStream(deploymentId, definition.getResourceName());
        // 6.文件的保存
        File filePng = new File("C:/File/evevtion/evection.png");
        File fileBpmn = new File("C:/File/evevtion/evection.bpmn");
        FileOutputStream pngOut = new FileOutputStream(filePng);
        FileOutputStream bpmnOut = new FileOutputStream(fileBpmn);

        IOUtils.copy(pngInput, pngOut);
        IOUtils.copy(bpmnInput, bpmnOut);

        pngInput.close();
        pngOut.close();
        bpmnInput.close();
        bpmnOut.close();
    }

    /**
     * @Description: 流程历史查看
     * @Date: 2023/12/1
     */
    @Test
    public void test11() {
        ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
        // 查看历史信息我们需要通过 HistoryService来实现
        HistoryService historyService = engine.getHistoryService();
        // 获取 actinst 对象
        HistoricActivityInstanceQuery instanceQuery = historyService.createHistoricActivityInstanceQuery();
        instanceQuery.processDefinitionId("evection:1:4");
        instanceQuery.orderByHistoricActivityInstanceStartTime().asc();
        List<HistoricActivityInstance> list = instanceQuery.list();
        // 输出查询的结果
        list.stream().forEach(historicActivityInstance -> {
            System.out.println(historicActivityInstance.getActivityId());
            System.out.println(historicActivityInstance.getActivityName());
            System.out.println(historicActivityInstance.getActivityType());
            System.out.println(historicActivityInstance.getAssignee());
            System.out.println(historicActivityInstance.getProcessDefinitionId());
            System.out.println(historicActivityInstance.getProcessInstanceId());
            System.out.println("-----------------------");
        });
    }

}
