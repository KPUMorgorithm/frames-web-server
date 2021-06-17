// This is for able to see chart. We are using Apex Chart. U can check the documentation of Apex Charts too..
var stompClient = null;
var username = null;
var totalDanger;
var totalNormal;
var totalWarning;
var set1;

var totalMember = $('#totalMember');
var memberIn = $('#memberIn');
var facility = $('#facility');
var numDanger = $('#danger');
var numNormal = $('#normal');
var numWarning = $('#warning');
var numTotal = $('#total');
'use strict';
var connectingElement = document.querySelector('.connecting');

function connect() {


    console.log("connect!!!!*****");
    //sockjs not defined 문제는 해당 <script>를 포함 안 시켜서 그런다
    var socket = new SockJS('/ws');

    stompClient = Stomp.over(socket);

    stompClient.connect({}, onConnected, onError);

}


function onConnected() {
    // Subscribe to the Public Topic
    stompClient.subscribe('/topic/public2', onMessageReceived);

    // Tell your username to the server


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
    stompClient.send("/app/status.sendMessage2", {}, JSON.stringify(chatMessage));

    console.log("message send 메시지 전송!!");
    setTimeout(sendMessage, 3000);

}


function onMessageReceived(payload) {
    var message = JSON.parse(payload.body);
    totalDanger = message.dangerScanNum;
    totalWarning = message.warningScanNum;
    totalNormal = message.normalScanNum;


    var testdate = $("#date").val()
    $("#totalMember").val(10);
    $("#memberIn").val(10);
    totalMember.val(message.totalMember);
    memberIn.val(message.inMember);
    facility.val(message.numOfFacility);

    numDanger.val(message.dangerScanNum);
    numNormal.val(message.normalScanNum);
    numWarning.val(message.warningScanNum);
    numTotal.val(message.totalScanNum);

    set1 = [{
        "name": "Danger",
        "color": "#fe460e",
        "y": totalDanger
    }, {

        "name": "Warning",
        "y": totalWarning,
        "color": "#ffa600"
    }, {

        "name": "Normal",
        "y": totalNormal,
        "color": "#01ff4a"
    }]
    console.log("#####DateValue:" + testdate);
    console.log("####value:" + totalDanger);
    console.log("####value:" + totalNormal);
    console.log("####value:" + totalWarning);
}

//이걸 풀어야 websocket 시작
connect();

/*
console.log("####value:"+totalDanger);
console.log("####value:"+totalNormal);
console.log("####value:"+totalWarning);*/

$(function () {
    $(document).ready(function () {
        Highcharts.setOptions({
            global: {
                useUTC: false
            }
        });


        $('#container').highcharts({
            chart: {
                plotBackgroundColor: null,
                plotBorderWidth: null,
                plotShadow: false,
                type: 'pie',
                events: {
                    load: function () {
                        // set up the updating of the chart each second
                        var series1 = this.series[0];


                        setInterval(function () {
                            series1.setData(set1);
                        }, 1000);
                    }
                }
            },
            title: {
                text: 'Real-Time Events'
            },
            tooltip: {
                pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b>'
            },
            accessibility: {
                point: {
                    valueSuffix: '%'
                }
            },
            plotOptions: {
                pie: {
                    allowPointSelect: true,
                    cursor: 'pointer',
                    dataLabels: {
                        enabled: true,
                        format: '<b>{point.name}</b>: {point.percentage:.1f} %'
                    }
                }
            },
            series: [{
                name: 'Events',
                colorByPoint: true,
                data: [{
                    name: 'Danger',
                    y: 30,
                }, {
                    name: 'Warning',
                    y: 30
                }, {
                    name: 'Normal',
                    y: 30,
                    sliced: true,
                    selected: true
                }]
            }]
        });
    });
});