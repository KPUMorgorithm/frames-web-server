package org.morgorithm.frames.service;

import com.google.common.collect.Lists;
import com.querydsl.core.BooleanBuilder;
import lombok.RequiredArgsConstructor;
import net.nurigo.java_sdk.api.Message;
import net.nurigo.java_sdk.exceptions.CoolsmsException;
import org.json.simple.JSONObject;
import org.morgorithm.frames.entity.QStatus;
import org.morgorithm.frames.entity.Sms;
import org.morgorithm.frames.entity.Status;
import org.morgorithm.frames.repository.MemberRepository;
import org.morgorithm.frames.repository.SmsRepository;
import org.morgorithm.frames.repository.StatusRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

@RequiredArgsConstructor
@Service
public class SmsPathRequest {
    private final SmsRepository smsRepository;
    private final MemberRepository memberRepository;
    private final StatusRepository statusRepository;
    private Long rnum;
    private String api_key = "NCSJBLIL70QSO6P9";
    //사이트에서 발급 받은 API KEY
    private String api_secret = "4BDRU03ULDUDLOY9BZ7AZIOLXAALBSUW";
    // 사이트에서 발급 받은 API SECRET KEY
    private Message coolsms = new Message(api_key, api_secret);

    @Scheduled(cron = "*/5 * * * * *")
    public void sendPathSms() {
        System.out.println("send Path Sms Call");
        List<Object[]> result = smsRepository.getAllList();

        for (Object[] a : result) {

            Boolean flag = ((Sms) a[0]).getStatus();
            if (flag) {
                sendSmsAuthentication(((Sms) a[0]).getSender(), ((Sms) a[0]).getContent());
                smsRepository.deleteById(((Sms) a[0]).getRno());
            } else {
                smsRepository.deleteById(((Sms) a[0]).getRno());
            }
        }
    }

    void sendSmsAuthentication(String phoneNum, String content) {
        //  System.out.println("phoneNum:" + phoneNum);
        String pn = phoneNum.substring(0, 3) + "-" + phoneNum.substring(3, 7) + "-" + phoneNum.substring(7, phoneNum.length());
        //  System.out.println("pn:" + pn);
        List<Object[]> result = null;
        result = memberRepository.getNameByPhone(pn);

        for (Object[] a : result) {
            String name = ((String) a[0]);
            //구성원 이름이랑 sender 이름이 같을 때
            System.out.println("name:" + name + " content:" + content);
            if (name.equals(content)) {
                // System.out.println("name과 content가 같음");
                System.out.println("mno:" + ((Long) a[1]));
                sendSms((Long) a[1], phoneNum);
            }
            //같지 않을 때
            else {
                System.out.println("mno:" + ((Long) a[1]));
                System.out.println("denied");
                sendSmsDenied((Long) a[1], phoneNum);
            }
        }
    }

    void sendSmsDenied(Long mno, String phoneNum) {
        HashMap<String, String> params = new HashMap<String, String>();

        params.put("from", "01096588541");

        params.put("type", "LMS");
        String s = "";
        //여럿한테 보낼때
        s += "한국산업기술대학교 코로나 비상대책본부에서 문자드립니다.\n" +
                "송신된 핸드폰 번호와 이름이 일치하지 않거나 등록되지 않은 구성원입니다.\n" +
                "[문자 내용: 홍길동(본인 이름)]만 보내주시기 바랍니다. \n";

        params.put("text", s);
        params.put("app_version", "test app 1.2");

        params.put("to", phoneNum);

        try {
            JSONObject obj = (JSONObject) coolsms.send(params);
            System.out.println(obj.toString());
        } catch (CoolsmsException e) {
            System.out.println(e.getMessage());
            System.out.println(e.getCode());
        }
        s = "";
        phoneNum = "";
    }

    void sendSms(Long mno, String phoneNum) {
        System.out.println("Mno:" + mno);
        BooleanBuilder booleanBuilder = getSearch(mno);

        Iterable<Status> result = statusRepository.findAll(booleanBuilder);
        List<Status> mapInfoList = Lists.newArrayList(result);
        mapInfoList.sort(Comparator.comparing(Status::getRegDate));

        for (Status s : mapInfoList) {
            System.out.println("Building:" + s.getFacility().getBuilding() + " date:" + s.getRegDate() + " status:" + s.getState());
        }

        HashMap<String, String> params = new HashMap<String, String>();

        params.put("from", "01096588541");

        params.put("type", "LMS");

        String s = "";
        if (mapInfoList.size() == 0) {
            s += "한국산업기술대학교 코로나 비상대책본부에서 문자드립니다.\n" +
                    "해당 수신자의 과거 7일간의 동선 데이터가 없음을 알려드립니다.\n";
        } else {
            //여럿한테 보낼때
            s += "한국산업기술대학교 코로나 비상대책본부에서 문자드립니다.\n" +
                    "해당 수신자의 과거 7일간의 동선을 알려드립니다.\n\n";

            for (Status p : mapInfoList) {

                String dateTime = p.getRegDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                s += "시설: " + p.getFacility().getBuilding() + "\n" +
                        "시간: " + dateTime + "\n" +
                        "출입여부: " + (p.getState() ? "입장\n\n" : "퇴장\n\n");
            }
        }

        System.out.println("phoneNum:" + phoneNum);
        params.put("text", s);
        params.put("app_version", "test app 1.2");

        params.put("to", phoneNum);

        try {
            JSONObject obj = (JSONObject) coolsms.send(params);
            System.out.println(obj.toString());
        } catch (CoolsmsException e) {
            System.out.println(e.getMessage());
            System.out.println(e.getCode());
        }
        s = "";
        phoneNum = "";
    }

    private BooleanBuilder getSearch(Long no) {

        //최근 7일동안의 경로를 메시지 보내준다.
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime sevenDaysPast = LocalDateTime.now().minusDays(7L);

        System.out.println("now:" + now);
        System.out.println("minus one:" + sevenDaysPast);

        BooleanBuilder booleanBuilder = new BooleanBuilder();

        QStatus qStatus = QStatus.status;

        List<Object[]> result = null;

        booleanBuilder.and(qStatus.regDate.between(sevenDaysPast, now));
        booleanBuilder.and(qStatus.member.mno.eq(no));

        //System.out.println(booleanBuilder.toString());

        return booleanBuilder;
    }
}
