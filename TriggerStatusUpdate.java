import com.example.studentselectionsystem.entity.Award;
import com.example.studentselectionsystem.mapper.AwardMapper;
import com.example.studentselectionsystem.scheduler.AwardStatusScheduler;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Date;
import java.util.List;

public class TriggerStatusUpdate {
    public static void main(String[] args) {
        // 创建Spring上下文
        ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
        
        // 获取AwardStatusScheduler实例
        AwardStatusScheduler scheduler = context.getBean(AwardStatusScheduler.class);
        
        // 调用更新方法
        System.out.println("开始更新奖项状态...");
        scheduler.updateAwardStatuses();
        System.out.println("奖项状态更新完成！");
        
        // 获取AwardMapper实例
        AwardMapper awardMapper = context.getBean(AwardMapper.class);
        
        // 查询所有奖项，显示更新后的状态
        List<Award> awards = awardMapper.selectList(null);
        System.out.println("\n更新后的奖项状态：");
        System.out.println("ID\t奖项名称\t\t状态\t\t当前状态\t\t结束时间");
        System.out.println("--------------------------------------------------------------------");
        for (Award award : awards) {
            System.out.printf("%d\t%s\t\t%s\t\t%s\t\t%s\n",
                    award.getId(),
                    award.getAwardName(),
                    award.getStatus(),
                    award.getCurrentStatus(),
                    award.getEndTime() != null ? award.getEndTime() : "-"
            );
        }
    }
}