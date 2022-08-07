package com.wang.tradecenter.stateMachine.statue;

import com.wang.tradecenter.entity.BO.Order;
import com.wang.tradecenter.stateMachine.OrderMachine;
import com.wang.tradecenter.stateMachine.SendFunction;
import org.springframework.stereotype.Service;

/**
 * @Auther: wAnG
 * @Date: 2022/3/24 20:23
 * @Description: 状态未变更直接丢回MQ进行轮转
 */

@Service
public class NoChangeStatue implements OrderMachine {

    @Override
    public void schedule(Order order, SendFunction function) {
        function.Send(order);
    }

}
