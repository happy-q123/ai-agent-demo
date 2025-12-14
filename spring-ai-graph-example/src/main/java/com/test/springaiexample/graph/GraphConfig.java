package com.test.springaiexample.graph;

import com.alibaba.cloud.ai.graph.*;
import com.alibaba.cloud.ai.graph.action.AsyncEdgeAction;
import com.alibaba.cloud.ai.graph.exception.GraphStateException;
import com.alibaba.cloud.ai.graph.state.strategy.ReplaceStrategy;
import com.test.springaiexample.entity.EnglishEntity;
import com.test.springaiexample.graph.node.TranslateWordNode;
import com.test.springaiexample.service.WordTranslationServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.swing.text.html.parser.Entity;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Configuration
public class GraphConfig {

    @Bean
    CompiledGraph englishTranslatonCompiledGraph(EnglishTranslateGraphNodes nodes) throws GraphStateException {

        //定义图的数据（state）
        KeyStrategyFactory keyStrategyFactory=new KeyStrategyFactoryBuilder()
                //数据名字为EnglishEntity，更新策略为Replace
                .addStrategies(Map.of("EnglishEntity",new ReplaceStrategy()))
                .build();

        //创建图
        StateGraph stateGraph = new StateGraph(keyStrategyFactory);

        //添加节点
        stateGraph.addNode("translateWord", nodes.getTranslateWordNode());
        stateGraph.addNode("translateJuzi", nodes.getTranslateJuziNode());
        stateGraph.addNode("composeJuzi", nodes.getComposeJuziNode());

//        stateGraph.addConditionalEdges("translateWord", AsyncEdgeAction.edge_async())

        // 一个节点的下一步不能同时拥有普通边和条件边
        //添加边
        stateGraph.addEdge(StateGraph.START,"translateWord" );

        /*
        // 添加一个条件边,语法如下
        graph.addConditionalEdges(
            "check_node", // 源节点
            new ConditionEvaluatorAction(), // 使用这个类来评估去哪里
            Map.of(       // 路由映射表
                "approve", "process_node",  // 如果返回 "approve"，去 process_node
                "reject",  "end_node",      // 如果返回 "reject"，去 end_node
                "default", "retry_node"     // 如果没找到结果，去 retry_node
            )
        );
        * */
        stateGraph.addConditionalEdges("translateWord", new AsyncEdgeAction() {
            @Override
            public CompletableFuture<String> apply(OverAllState state) {
                EnglishEntity entity= (EnglishEntity) state.value("EnglishEntity").get();
                if (entity.getWordTranslation()==null)
                    return CompletableFuture.completedFuture("isNull");
                else {
                    System.out.println("*********notNull*************");
                    return CompletableFuture.completedFuture("notNull");
                }
            }
        }, Map.of("isNull", "translateJuzi","notNull", "composeJuzi"));


        stateGraph.addEdge("translateJuzi",StateGraph.END);
        stateGraph.addEdge("composeJuzi",StateGraph.END);

        return stateGraph.compile();
    }
}

//顺序执行版
//package com.test.springaiexample.graph;
//
//import com.alibaba.cloud.ai.graph.*;
//import com.alibaba.cloud.ai.graph.action.AsyncEdgeAction;
//import com.alibaba.cloud.ai.graph.exception.GraphStateException;
//import com.alibaba.cloud.ai.graph.state.strategy.ReplaceStrategy;
//import com.test.springaiexample.graph.node.TranslateWordNode;
//import com.test.springaiexample.service.WordTranslationServiceImpl;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import java.util.List;
//import java.util.Map;
//
//@Configuration
//public class GraphConfig {
//
//    @Bean
//    CompiledGraph englishTranslatonCompiledGraph(EnglishTranslateGraphNodes nodes) throws GraphStateException {
//
//        //定义图的数据（state）
//        KeyStrategyFactory keyStrategyFactory=new KeyStrategyFactoryBuilder()
//                //数据名字为EnglishEntity，更新策略为Replace
//                .addStrategies(Map.of("EnglishEntity",new ReplaceStrategy()))
//        .build();
//
//        //创建图
//        StateGraph stateGraph = new StateGraph(keyStrategyFactory);
//
//        //添加节点
//        stateGraph.addNode("translateWord", nodes.getTranslateWordNode());
//        stateGraph.addNode("translateJuzi", nodes.getTranslateJuziNode());
//        stateGraph.addNode("composeJuzi", nodes.getComposeJuziNode());
//
////        stateGraph.addConditionalEdges("translateWord", AsyncEdgeAction.edge_async())
//
//        //添加边
//        stateGraph.addEdge(StateGraph.START,"translateWord" );
//        stateGraph.addEdge("translateWord","composeJuzi");
//        stateGraph.addEdge("composeJuzi","translateJuzi");
//        stateGraph.addEdge("translateJuzi",StateGraph.END);
//
//        return stateGraph.compile();
//    }
//}
