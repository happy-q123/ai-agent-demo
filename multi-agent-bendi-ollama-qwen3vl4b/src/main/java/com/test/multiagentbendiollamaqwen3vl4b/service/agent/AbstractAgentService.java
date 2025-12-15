package com.test.multiagentbendiollamaqwen3vl4b.service.agent;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.api.Advisor;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractAgentService implements AgentService{
    protected ChatClient chatClient;

    protected List<Advisor> advisors;
    protected Long advisorVersion;

    @Getter
    protected String agentName;

    @Getter
    protected String agentDescription;

    @Getter
    protected String systemPrompt;

    public AbstractAgentService(ChatClient.Builder chatClientBuilder) {
        initProperties();
        initChatClient(chatClientBuilder);
    }

    /**
     * description 初始化各种属性
     * author zzq
     * date 2025/12/14 21:51
     * param
     * return
     */
    protected void initProperties(){
        this.agentDescription="";
        this.agentName="";
        this.systemPrompt="";
    }

    /**
     * description 初始化chatClient
     * author zzq
     * date 2025/12/14 21:42
     * param
     * return
     */
    protected abstract void initChatClient(ChatClient.Builder chatClientBuilder);

    /**
     * description 初始化Advisor
     * author zzq
     * date 2025/12/14 22:52
     * param
     * return
     */
    protected void initAdvisors() {}

    /**
     * description 重新设置chatClient
     * author zzq
     * date 2025/12/14 21:45
     * param
     * return
     */
    public void resetChatClient(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    /**
     * description 重设默认系统提示
     * author zzq
     * date 2025/12/14 21:47
     * param prompt：新的系统提示词
     * return
     */
    public void resetDefaultSystemPrompt(String prompt) {
        this.chatClient = chatClient.mutate()
                .defaultSystem(prompt)
                .build();
        this.systemPrompt = prompt;
    }

    /**
     * description 重设agent的描述
     * author zzq
     * date 2025/12/14 21:47
     * param
     * return
     */
    public void resetAgentDescription(String description) {
        this.agentDescription=description;
    }


    /**
     * description 刷新advisor
     * author zzq
     * date 2025/12/14 23:01
     * param
     * return
     */
    public void flushChatClient(){
        if (advisors==null){
            chatClient=chatClient.mutate()
                    .build();
        }else {
            chatClient=chatClient.mutate()
                    .defaultAdvisors(advisors)
                    .build();
        }
    }

    public void addAdvisor(Advisor advisor){
        if (advisors==null)
            advisors=new ArrayList<>();
        advisors.add(advisor);
        flushChatClient();
    }

    public void removeAdvisor(Advisor advisor){
        if (advisors==null)
            return;
        advisors.remove(advisor);
        flushChatClient();
    }

    public void removeAllAdvisor(){
        advisors=null;
        flushChatClient();
    }

    public List<Advisor> getAdvisors(){
        return new ArrayList<>(advisors);
    }
}