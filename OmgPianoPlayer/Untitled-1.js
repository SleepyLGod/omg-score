var  a=["1","2","3","4","5"];
var  b=["a","b","c","d","e"];
var c={};
var d=new Array();
var g=new Array();
var e=new Array();
for (i=0;i<5;i++){
    d.push({"s":a[i],"z":b[i]});
    g.push({"z":a[4-i],"m":b[4-i]});
}
c["d"]=d;
c["g"]=g;
e.push(d);
e.push(g);
for (i=0;i<c.d.length;i++){
    var f=c.d[i];
    console.log(f.s)
}

$.ajax({
    url:"addJson",
    data:JSON.stringify(c),
    contentType :"application/json;charsetset=UTF-8",//必须
    dataType:"json",//必须
    type:"post",
    success:function (data) {
        alert(data)
    },error:function () {
        alert(222)
    }

})
