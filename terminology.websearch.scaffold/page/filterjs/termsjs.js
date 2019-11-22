var fJS;

function initFilterList(listLabel, data, includeNone){
  var checkBoxList=$('#'+listLabel);
  if(includeNone){
    var append="<li><input id='"+listLabel+"none' value='none' type='checkbox'><span id='filter_"+listLabel+"none_label'>missing localization</span></li>";
    checkBoxList.append(append);
  }
  for(var i=0; i<data.length; i++){
    var filter=data[i];
    var append="<li><input id='"+listLabel+filter.name+"' value='"+filter.name+"' type='checkbox'><span>"+filter.display+"</span></li>";
    checkBoxList.append(append);
  }
  if(data.length==0 && includeNone || data.length==1 && !includeNone){
    checkBoxList.parent()[0].style.display='none';
  }
  var header=$('#filter_'+listLabel+'_label');
  header.click(function () {
    checkBoxList.slideToggle(0, function () {
      var filterBarHeight=$('.sidebar_bar').height();
      $('.featured_services_find').css('max-height', filterBarHeight+'px');
      $('#details_service').css('max-height', filterBarHeight+'px');
      header.toggleClass("collapsed");
    });
  });
  header.click();
};

function toggleAll(toggleId){
  var checkboxSelector='#'+toggleId +' :checkbox';
  var toggleSelector='#toggle'+toggleId;
  var size=$(checkboxSelector).size();
  if(size<2){
    $(toggleSelector)[0].style.display='none';
  }else{
	  $(toggleSelector).on('click', function(){
	    var toggleButtonId='#toggle'+toggleId;
	    var checkboxSelector='#'+toggleId +' :checkbox';
	    var onOff=$(toggleButtonId).is(":checked");
	    var noneCheckbox='#'+toggleId+'none';
	    $(checkboxSelector).prop('checked', onOff);
	    $(noneCheckbox).prop('checked', true);
	    fJS.filter();//this seems not to be necessary in the example!?
	  });
  }
};

function unselectResultList(){
  $('.resultlist').removeClass('result_selected');
}

jQuery(document).ready(function($) {

  $('.header_name').html(terminology.name);
  initFilterList("customers",terminology.customers, true);
  initFilterList("products",terminology.products, true);
  initFilterList("languages",terminology.languages, false);
  initFilterList("subjects",terminology.subjects, false);

  $('#termstatus :checkbox').prop('checked', true);
  $('#customers :checkbox').prop('checked', true);
  $('#products :checkbox').prop('checked', true);
  $('#languages :checkbox').prop('checked', true);
  $('#subjects :checkbox').prop('checked', true);

  $('#searchAlso input').on('click', function(){
    unselectResultList();
    fJS.initSearch({ele: '#search_box', fields:getSearchFields()});
    fJS.filter();
  });

  $('input').on('change', function(){
    $('#details').html('');
    unselectResultList();
  });

  $('#search_box').on('keypress', function(e){
    $('#details').html('');
    unselectResultList();
  });

  toggleAll('products'); 
  toggleAll('customers'); 
  toggleAll('subjects'); 

  language;//touch localize.js for initializing localization
  fJS=filterInit($);
});

function loadEntry(id, termid){
  $('#details').load('data/terms.html #'+id, function(){
    localizeDetails(language);
    unselectResultList();
    $("#result"+termid).addClass("result_selected");
  });
};

function renderTerm(jsonTerm){
  var action="onclick=loadEntry('"+jsonTerm.entry_id+"','"+jsonTerm.id+"')>";
  var id='id="result'+jsonTerm.id+'" ';
  return "<span "+id+"class='resultlist "+jsonTerm.term_status+"' "+action+jsonTerm.term+"</span>";
};

function getSearchFields(){
   var searchFields=['term'];
   var searchInUsage=$('#usage').is(":checked");
   var searchInDefinition=$('#definition').is(":checked");
   if(searchInUsage){
     searchFields.push('usage');
   }
   if(searchInDefinition){
     searchFields.push('entry_definition');
   }
   return searchFields;
};

function filterInit($) {
   var view = function(jsonTerm){
     var result="<div class='result_term'>" + renderTerm(jsonTerm) + "</div>";
     return result;
   };

  var settings = {
    criterias: [
      {ele: '#termstatus :checkbox', field:'term_status'},
      {ele: '#languages :checkbox', field:'language'},
      {ele: '#subjects :checkbox', field:'subject'},
      {ele: '#customers :checkbox', field:'customers.id'},
      {ele: '#products :checkbox', field:'products.id'},
    ],
    view: view,
    template:'#template',
    search: {ele: '#search_box', fields:getSearchFields()},
    and_filter_on: true,
    id_field: 'id' //Default is id. This is only for usecase
  };

  return FilterJS(data, "#service_list", settings);
}
