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
	  $(toggleSelector).on('change', function(){
	    var toggleButtonId='#toggle'+toggleId;
	    var checkboxSelector='#'+toggleId +' :checkbox';
	    var onOff=$(toggleButtonId).is(":checked");
	    var noneCheckbox='#'+toggleId+'none';
	    $(checkboxSelector).prop('checked', onOff);
	    $(noneCheckbox).prop('checked', true);

	    $('#service_list').html('');
	    fJS.clear();
	    fJS = filterInit($);
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

  $('#searchAlso input').on('change', function(){
    $('#service_list').html('');
    fJS.clear();
    unselectResultList();
    fJS = filterInit($);
  });

  $('input').on('change', function(){
    $('#details').html('');
    unselectResultList();
  });

  $('#search_box').on('keypress', function(){
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

function filterInit($) {
   var searchInUsage=$('#usage').is(":checked");
   var searchInDefinition=$('#definition').is(":checked");

   var view = function(jsonTerm){
   var result="<div class='result_term'>" +
        renderTerm(jsonTerm) + "<div style='display: none;'>";
   if(searchInUsage){
     result=result+" "+jsonTerm.usage;
   }
   if(searchInDefinition){
     result=result+" "+jsonTerm.entry_definition;
   }
   result=result+"</div></div>";
   return result;
  };

  var settings = {
    filter_criteria: {
      termstatus: ['#termstatus :checkbox', 'term_status'],
      languages: ['#languages :checkbox', 'language'],
      subjects: ['#subjects :checkbox', 'subject'],
      customers: ['#customers :checkbox', 'customers.ARRAY.id'],
      products: ['#products :checkbox', 'products.ARRAY.id']
    },
    search: {input: '#search_box'},
    and_filter_on: true,
    id_field: 'id' //Default is id. This is only for usecase
  };

  return FilterJS(data, "#service_list", view, settings);
}
