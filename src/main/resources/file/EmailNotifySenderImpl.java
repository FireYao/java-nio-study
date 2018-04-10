package com.simpletour.mail.api.sender;

import com.simpletour.commons.enums.StaffNotifyTemplate;
import com.simpletour.entity.company.CorpStaffNotify;
import com.simpletour.mail.api.notify.BaseNotifyItem;
import com.simpletour.mail.api.notify.ManagedResourceRefundNotify;
import com.simpletour.mail.api.notify.OrderFailedNotify;
import com.simpletour.mail.api.notify.RefundFailedNotify;
import com.simpletour.mail.api.support.EmailNotifySenderTool;
import com.simpletour.repository.company.CorpStaffNotifyRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
public class EmailNotifySenderImpl implements EmailNotifySender {

    private JavaMailSenderImpl javaMailSender;

    private EmailNotifySenderTool emailNotifySenderTool;

    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Resource
    private CorpStaffNotifyRepository corpStaffNotifyRepository;

    public EmailNotifySenderImpl(JavaMailSenderImpl javaMailSender, ThreadPoolTaskExecutor threadPoolTaskExecutor) {
        this.javaMailSender = javaMailSender;
        this.threadPoolTaskExecutor = threadPoolTaskExecutor;
        this.emailNotifySenderTool = new EmailNotifySenderTool(javaMailSender);
    }

    @Override
    public void notifyWhenRefundFailed(RefundFailedNotify notify) throws Exception {
        executor(notify,StaffNotifyTemplate.REFUND_FAILURE);
    }

    @Override
    public void notifyWhenOrderFailed(OrderFailedNotify notify) throws Exception {
        executor(notify,StaffNotifyTemplate.ORDER_FAILURE);
    }

    @Override
    public void notifyWhenManagedResourceRefundNotify(ManagedResourceRefundNotify notify) throws Exception {
        executor(notify,StaffNotifyTemplate.MANAGED_RESOURCE_REFUND);
    }


    private String[] getEmails(Long companyId, StaffNotifyTemplate template) {
        List<CorpStaffNotify> notifies = corpStaffNotifyRepository.findByCompanyId(companyId).stream().filter(n -> n.getNofifyTemplate().contains(template)).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(notifies)) {
            return null;
        }
        List<String> emails = notifies.stream().map(CorpStaffNotify::getEmail).collect(Collectors.toList());
        return emails.toArray(new String[emails.size()]);
    }


    private void executor(BaseNotifyItem notify,StaffNotifyTemplate template){
        threadPoolTaskExecutor.execute(() ->
                emailNotifySenderTool.doNotify(
                        notify.getCompanyId(),
                        emailNotifySenderTool.getTemplateHtml(template.getTemplateUrl(), notify),
                        template.formatTitle(notify.getResourceName(), notify.getType(), notify.getBookingDay(), notify.getVendorName()),
                        getEmails(notify.getCompanyId(), template))
        );
    }

}
