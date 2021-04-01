
$(function () {
    $(document).ready(function () {
        Highcharts.setOptions({
            global: {
                useUTC: false
            }
        });

        $('#container').highcharts({
            title: {
                text: '일일 확진자 수'
            },
            subtitle: {
                text: '전체 확진자 수: '+listTotalConfirmed
            },

            xAxis: {
                // categories: category
                categories: listDates,

                title: {
                    text: '날짜'
                }

            },
            yAxis:[{
                labels:{
                    style: {
                        color: "#00bfff"
                    }
                },
                title: {
                    text: '일일 확진자 수',
                    style: {
                        color: "#00bfff"
                    }
                }
            },{ // Primary yAxis
                labels: {
                    style: {
                        color: Highcharts.getOptions().colors[1]
                    }
                },
                title: {
                    text: '일일 사망자 수',
                    style: {
                        color: Highcharts.getOptions().colors[1]
                    }
                },
                opposite: true

            }],

            legend: {
                layout: 'vertical',
                align: 'right',
                verticalAlign: 'middle'
            },

            plotOptions: {
                series: {
                    label: {
                        connectorAllowed: false
                    },
                },

            },

            series: [{
                name: '일일 확진자 수',
                data: listIncreaseFromYesterday
            },{
                name: '사망자 수',
                yAxis:1,
                data: listDailyDeath
            },],
            //highchart.com 안나오게 하기
            credits:{
                enabled:false
            },


            responsive: {
                rules: [{
                    condition: {
                        maxWidth: 500
                    },
                    chartOptions: {
                        legend: {
                            layout: 'horizontal',
                            align: 'center',
                            verticalAlign: 'bottom'
                        }
                    }
                }]
            }
        });
    });
});
