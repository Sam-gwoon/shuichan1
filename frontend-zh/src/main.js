import Vue from 'vue';
import ElementUI from 'element-ui';
import App from './App.vue';
import router from './router';

Vue.use(ElementUI);

Vue.filter('capitalize', function(value) {
  if (!value) return '';
  return value.charAt(0).toUpperCase() + value.slice(1).toLowerCase();
});

Vue.config.productionTip = false;

new Vue({
  router,
  render: h => h(App)
}).$mount('#app');
