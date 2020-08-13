package com.behavior.scheduler;

import java.util.List;
import java.util.Map;

/**
 * @author cobin(Cobin)
 * @desc TODO
 * @Date 2020/8/8 9:57
 */
public interface WorkCallExcute {
    void execute(List<List<Map<Object,Object>>> qData);
    String[] getKeys();
    String getTag();
    int getInserSize();
}
