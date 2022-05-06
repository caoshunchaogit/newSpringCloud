package com.ccc.controller;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import sun.dc.pr.PRError;

import java.util.List;

@RestController
public class MyController {

    @Autowired
    private DiscoveryClient client;
    @Autowired
    private EurekaClient client2;
    @Autowired
    private LoadBalancerClient loadBalancerClient;

    @GetMapping("/getHi")
    public String getHi(){
        return "getHi";
    }

    @GetMapping("client")
    public String getClient(){
        List<String> services = client.getServices();
        for (String s : services){
            System.out.println(s);
        }
        return "hi" ;
    }

    @GetMapping("client2")
    public Object getClient2(){
        return client.getInstances("eureka-consumer");  //服务名称
    }

    @GetMapping("client3")
    public Object getClient3(){
        List<ServiceInstance> instances = client.getInstances("eureka-provider");  //通过服务名
        for(ServiceInstance instance : instances){
            System.out.println(ToStringBuilder.reflectionToString(instance));
        }
        return  instances; //服务名称
    }

    @GetMapping("client4")
    public Object getClient4(){
       //List instancesById1 = client2.getInstancesById(" localhost:eureka-server:7001");  具体的服务
        //通过服务名。找列表
        List<InstanceInfo> instancesById = client2.getInstancesByVipAddress("eureka-provider",false);
        if(instancesById.size() > 0){
            InstanceInfo instanceInfo = instancesById.get(0);
            if (instanceInfo.getStatus() == InstanceInfo.InstanceStatus.UP){
                String url = "http://" + instanceInfo.getHostName() + ":" + instanceInfo.getPort() + "/getHi";
                System.out.println(url);

                RestTemplate restTemplate = new RestTemplate();
                String forObject = restTemplate.getForObject(url, String.class);
                System.out.println(forObject);
            }
        }
        return  instancesById; //服务名称
    }

    @GetMapping("client5")
    public Object getClient5(){
        //Ribbon 完成客户端的负载均衡的策略过滤掉down的机器
        ServiceInstance choose = loadBalancerClient.choose("eureka-provider");  //通过负载均衡选择一个服务 Ribbon会过滤掉
        String url = "http://" + choose.getHost() + ":" + choose.getPort() + "/getHi";
        System.out.println(url);
        RestTemplate restTemplate = new RestTemplate();
        String forObject = restTemplate.getForObject(url, String.class);

        return  url + forObject; //服务名称
    }
}
