(function(){
    /**
     * 初始化 Tabs
     */
    function initTabs() {
        const tabBarEl = document.querySelector("#loginTabBar");
        if (!tabBarEl) return;
        let loginTabBar  = new mdc.tabBar.MDCTabBar(tabBarEl);

        const tabs = document.querySelectorAll('.mdc-tab');
        const panels = document.querySelectorAll('.tab-panel');

        loginTabBar.listen('MDCTabBar:activated', (event) => {
            const activeTabIndex = event.detail.index;
            const activeTab = tabs[activeTabIndex];
            // 获取tab绑定的panel标识
            const panelKey = activeTab.dataset.tab;
            // 激活对应面板
            panels.forEach(panel => panel.classList.remove('active'));
            const targetPanel = document.querySelector(`.tab-panel[data-panel="${panelKey}"]`);
            if (targetPanel) targetPanel.classList.add('active');
        });
    }

    document.addEventListener('DOMContentLoaded', function () {
        initTabs()
    })
}())