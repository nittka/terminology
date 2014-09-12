language='en';

localization={
  en:{
    filter:{
      filter_search_label:"Search",
      filter_searchUsage_label:"also in Usage",
      filter_searchDefinition_label:"also in Definition",
      filter_termstatus_label:"Term status",
      filter_preferred_label:"preferred",
      filter_permitted_label:"permitted",
      filter_superseded_label:"superseded",
      filter_rejected_label:"rejected",
      filter_customers_label:"Customers",
      filter_products_label:"Products",
      filter_customersnone_label:"none",
      filter_productsnone_label:"none"
    },
    details:{
      subject_label:"Category:",
      entry_status_label:"Entry status:",
      definition_label:"Definition:",
      term_label:"Term:",
      language_label:"Language:",
      grammar_label:"Grammar:",
      usage_label:"Usage:",
      customers_label:"Customers:",
      products_label:"Products:",
      see_also_label:"See also:"
    }
  },
  de:{
    filter:{
      filter_search_label:"Suche",
      filter_searchUsage_label:"auch in Verwendung",
      filter_searchDefinition_label:"auch in Definition",
      filter_termstatus_label:"Termstatus",
      filter_preferred_label:"bevorzugt",
      filter_permitted_label:"erlaubt",
      filter_superseded_label:"veraltet",
      filter_rejected_label:"verboten",
      filter_customers_label:"Kunden",
      filter_products_label:"Produkte",
      filter_customersnone_label:"ohne",
      filter_productsnone_label:"ohne"
    },
    details:{
      subject_label:"Kategorie:",
      entry_status_label:"Eintragsstatus:",
      definition_label:"Definition:",
      term_label:"Term:",
      language_label:"Sprache:",
      grammar_label:"Grammatik:",
      usage_label:"Verwendung:",
      customers_label:"Kunden:",
      products_label:"Produkte:",
      see_also_label:"Siehe auch:"
    }
  }
};



function switchLanguage(lang){
  localizeFilters(lang);
  localizeDetails(lang);
};

function languageToggle(lang){
  return "<span class='lang' onclick=switchLanguage('"+lang+"')>"+lang+"</span>";
}

function initLanguageToggle(){
  $('#languageToggle').append(languageToggle("de"));
  $('#languageToggle').append(languageToggle("en"));
};

function languageToggle(lang){
  return "<span class='lang' onclick=switchLanguage('"+lang+"')>"+lang+"</span>";
};

function localizeFilters(locale){
  var loc=localization[locale].filter;
  for(var id in loc){
    $('#'+id).html(loc[id]);
  }
}

function localizeDetails(locale){
  var loc=localization[locale].details;
  for(var id in loc){
    $('.'+id).html(loc[id]);
  }
}

jQuery(document).ready(function($) {
  initLanguageToggle();
  localizeFilters(language);
  localizeDetails(language);
});
