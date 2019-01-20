//存放主要交互逻辑代码
//javascript模块化
var seckill={
    URL:{

    },
    validatePhone:function(phone){
       if( phone && phone.length==11){
           return true;
       }else {
           return false;
       }
    },

    detail:{
        //详情页初始化
        init:function (params) {
            //手机验证和登录，计时交互
            var killPhone = $.cookie('killPhone');
            var startTime = params['startTime'];
            var endTime = params['endTime'];
            var seckillId = params['seckillId'];
            //验证手机号
            if (!seckill.validatePhone(killPhone)){
                //绑定手机号码
                var killPhoneModal = $('#killPhoneModal');
                //显示弹出层
                killPhoneModal.modal({
                    show:true,//显示弹出层
                    backdrop:"static",//禁止位置关闭
                    keyboard:false//关闭键盘事件
                });
                $('#killPhoneBtn').click(function () {
                    var inputPhone = $('#killPhoneKey').val();
                    console.log('phone = '+inputPhone);//TODO
                    if (seckill.validatePhone(inputPhone)){
                        //电话写入cookie
                        $.cookie('killPhone',inputPhone,{expires:7,path:'/seckill'});
                        //刷新页面
                        window.location.reload();
                    } else {
                        $('#killPhoneMessage').hide().html('<label class="label label-danger">手机号码错误!</label>').show(300);
                    }
                });
            }
            //已经登录
        }
    }
}