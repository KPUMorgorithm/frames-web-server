
var arrIn;
var arrOut;
var arrBno;
var arrBName;
var table;
var statusList;
var compareTemp;
var cmp1=1;
var cmp2=2;

'use strict';


var stompClient = null;
var username = null;
var chatPage = document.querySelector('#chat-page');
var messageForm = document.querySelector('#messageForm');
var messageInput = document.querySelector('#message');
var messageArea = document.querySelector('#messageArea');
var connectingElement = document.querySelector('.connecting');
var colors = [
    '#2196F3', '#32c787', '#00BCD4', '#ff5652',
    '#ffc107', '#ff85af', '#FF9800', '#39bbb0'
];
function connect() {


    console.log("connect!!!!*****");
    var socket = new SockJS('/ws');
    chatPage.classList.remove('hidden');
    stompClient = Stomp.over(socket);

    stompClient.connect({}, onConnected, onError);

}


function onConnected() {
    // Subscribe to the Public Topic
    stompClient.subscribe('/topic/public', onMessageReceived);

    // Tell your username to the server

    connectingElement.classList.add('hidden');
    sendMessage();
}


function onError(error) {
    console.log("error");
    connectingElement.textContent = 'Could not connect to WebSocket server. Please refresh this page to try again!';
    connectingElement.style.color = 'red';
}


function sendMessage() {

    var chatMessage = {
        sender: "username",
        content: "messageInput.value",
        type: 'CHAT'
    };
    stompClient.send("/app/status.sendMessage", {}, JSON.stringify(chatMessage));

    console.log("message send 메시지 전송!!");
    setTimeout(sendMessage, 3000);

}


function onMessageReceived(payload) {
    var message = JSON.parse(payload.body);
    var messageElement = document.createElement('li');




    arrIn = message.in;
    arrOut = message.out;
    arrBno = message.bno;
    arrBName = message.bname;
    statusList=message.statusList;

    if(statusList.length!==0)
        cmp1=JSON.stringify(statusList);
    var t=cmp1===cmp2;
    console.log("**************equal?"+t);
    if(cmp1===cmp2)
        return;

    console.log("**************not equal");
    compareTemp=message.statusList;
    cmp1=JSON.stringify(statusList);
    cmp2=JSON.stringify(compareTemp);

   // compareTemp=message.statusList;
    var bName=[];
    var mno=[];
    var tem=[];
    var regDate=[];
    var entranceState=[];

    for(var i=0;i<statusList.length;i++){
        var object=statusList[i];
        bName.push(object.facility.building);
        mno.push(object.member.mno);
        tem.push(object.temperature);
        regDate.push(object.regDate);
        entranceState.push(object.state);
    }

    console.log(bName);
    console.log(mno);
    console.log(tem);
    console.log(regDate);
    console.log(entranceState);

  // var test2=tes10.mno;








    //var avatarElement = document.createElement('i');
   // var avatarText = document.createTextNode(message.sender[0]);
   // avatarElement.appendChild(avatarText);
   // avatarElement.style['background-color'] = getAvatarColor(message.sender);

  //  messageElement.appendChild(avatarElement);

  //  var usernameElement = document.createElement('span');
  //  var usernameText = document.createTextNode(message.sender);
 //   usernameElement.appendChild(usernameText);
  //  messageElement.appendChild(usernameElement);




    //실질적인 메시지 내용이 들어감
    for(var i=0;i<bName.length;i++){
        var textElement = document.createElement('p');
        //var horizontalElement=document.createElement('hr');
        var s;
        if(entranceState[i])
            s="입장";
        else
            s="퇴장";

        var messageText = document.createTextNode("건물:"+bName[i]+" 아이디:"+mno[i]+" 온도:"+tem[i]+" 시간:"+regDate[i]+" "+s);

        textElement.appendChild(messageText);

      //  messageElement.appendChild(horizontalElement);
        messageElement.appendChild(textElement);

        messageArea.appendChild(messageElement);
        messageArea.scrollTop = messageArea.scrollHeight;


    }

    //test section ***************************











    const table=document.getElementById('datatable');

    for(let j=0;j<10;j++){
        table.rows[j+1].cells[0].innerHTML=arrBName[j];
        table.rows[j+1].cells[1].innerHTML=arrIn[j];
        table.rows[j+1].cells[2].innerHTML=arrOut[j];
    }

}
//test function getAvatarColor
function getAvatarColor(messageSender) {
    var hash = 0;
    for (var i = 0; i < messageSender.length; i++) {
        hash = 31 * hash + messageSender.charCodeAt(i);
    }
    var index = Math.abs(hash % colors.length);
    return colors[index];
}


connect();


$(function () {
    $(document).ready(function () {
        Highcharts.setOptions({
            global: {
                useUTC: false
            }
        });

        $('#buildingStatus').highcharts({
            chart: {
                type: 'column',
                animation: Highcharts.svg, // don't animate in old IE
                marginRight: 10,
                events: {
                    load: function () {

                        // set up the updating of the chart each second
                        var series = this.series[0];
                        var series2=this.series[1];
                        var cate=this.xAxis[0];
                        setInterval(function () {
                            series.setData(arrIn);
                            series.setName("In");
                            series2.setData(arrOut);
                            series2.setName("Out");
                            cate.setCategories(arrBName);
                        }, 1000);
                    }
                }
            },
            title: {
                text: '건물 출입 현황'
            },
            yAxis: {
                title: {
                    text: '명수'
                },
                plotLines: [{
                    value: 0,
                    width: 1,
                    color: '#808080'
                }]
            },
            legend: {
                enabled: true
            },
            exporting: {
                enabled: false
            },
            credits:{
                enabled:false
            },

            series: [{
                data: [0,0,0,0,0,0,0,0,0,0],color:'#f89203',
                name: 'In',
                dataLabels: {
                    enabled: true,
                    color: '#e10000',
                    //inside: true
                }
            },{

                data: [0,0,0,0,0,0,0,0,0,0],color: '#0078e2',
                name: 'Out',
                dataLabels: {
                    enabled: true,
                    color: '#0000f3',
                    //inside: true
                }
            }]
        });
    });
});
