(function($){

    function formatArtifact (artifact){
        return artifact.groupId+':'+artifact.artifactId+':'+artifact.version;
    }
    
    $(document).ready(function(){
        $("#btDisplay").click(function(){
            var el = $("#viewport");
            clearFilters();
            el.html($('<h3>'+$("#groupId").val()+':'+$("#artifactId").val()+':'+$("#version").val()+'</h3>'));
            el.append($('<div id="loading"><p><img src="img/ajax-loader.gif" /> Downloading the Internet. Please Wait...</p></div>'));
            $.getJSON('api/tree/'+$("#groupId").val()+'/'+$("#artifactId").val()+'/'+$("#version").val(), function(data) {
                el.append($(html(data)));
                $("li").each(function(e){
                    var next =  $(this).next();
                    if(next && next[0] && next[0].localName =='ul'){     
                        $(this).addClass('animateable').click(function(){next.slideToggle(400);});
                    }
                    $(this).hover(
                        function(e){$(this).find('span').show();},
                        function(e){$(this).find('span').hide();}
                    );
                });
            }).error(function(data){
                if(data.status == 404){
                    el.append($('<p class="error"><span class="label label-important"> Artifact not found ('+data.status+')</span></p>'));
                }else{
                    el.append($('<p class="error"><span class="label label-important">'+data.statusText+' ('+data.status+'): </span>'+ data.responseText+'</p>'));
                }
            }).complete(function(){
                $('#loading').remove();
            });
            
        });
        $("#btClear").click(function(){
            $("#viewport").html('');
            clearFilters();
        });
        
        $("#scopetest").click(function(){
            $('.test').slideToggle(400);
        });
        
        $("#scoperuntime").click(function(){
            $('.runtime').slideToggle(400);
        });
        if($("#groupId").val && $("#artifactId").val() && $("#version").val()){
            // submit user data.
            $("#btDisplay").click();
        }
    })
    
    function clearFilters() {
        $("input[type='checkbox']").each(function (){
            $(this)[0].checked = true;
        });
    }
    
    function html(data){
        var res = '';
        if(data.children){
            res +='<ul>';
            data.children.forEach(function (art){
                res += '<li class="'+art.scope+'" title="scope :'+ art.scope +'">'+formatArtifact(art) +
                    '<span style="display:none"> <a title="show subtree starting from this artifact" href="./?groupId='+art.groupId+'&artifactId='+art.artifactId+'&version='+art.version+
                    '"><i class="icon-tags"></i></a></span>'
                    + '</li>';
                res += html(art);
            });
            res +='</ul>';
        }
        return res;
    }

})(this.jQuery)
