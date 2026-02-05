(function () {
    const token = localStorage.getItem("token");
    if (!token) {
        window.location.href = "/login.html";
        return;
    }

    // ========= 请求封装 =========
    async function request(path, { method = "GET", body } = {}) {
        const headers = {
            "Content-Type": "application/json",
            "Authorization": "Bearer " + token
        };
        const res = await fetch(path, {
            method,
            headers,
            body: body ? JSON.stringify(body) : undefined
        });

        if (res.status === 401 || res.status === 403) {
            localStorage.removeItem("token");
            window.location.href = "/login.html";
            return;
        }

        const text = await res.text();
        let data;
        try { data = text ? JSON.parse(text) : null; } catch { data = text; }

        if (!res.ok) {
            const msg = (data && (data.message || data.error)) || ("请求失败：" + res.status);
            throw new Error(msg);
        }
        return data;
    }

    function showHint(msg) {
        const el = document.getElementById("hintBox");
        if (el) el.textContent = msg || "";
    }

    // ========= 退出 =========
    document.getElementById("btnLogout")?.addEventListener("click", () => {
        localStorage.removeItem("token");
        window.location.href = "/login.html";
    });

    // ========= 菜单折叠/展开（默认展开）=========
    function initCollapsibleGroups() {
        const sidebar = document.querySelector(".sidebar");
        if (!sidebar) return;

        // ✅ 防止重复绑定
        if (sidebar.dataset.bound === "1") return;
        sidebar.dataset.bound = "1";

        sidebar.addEventListener("click", (e) => {
            const title = e.target.closest(".menu-group-title");
            if (!title) return;

            // ✅ 防止触发其它点击逻辑
            e.preventDefault();
            e.stopPropagation();

            const group = title.closest(".menu-group");
            if (!group) return;

            const expanded = group.getAttribute("data-expanded") !== "false";
            group.setAttribute("data-expanded", expanded ? "false" : "true");

            // ✅ 立刻打印，确认每次都执行到了
            console.log("[toggle]", group.dataset.group, "=>", group.getAttribute("data-expanded"));
        });
    }




    // ========= 菜单切换 =========
    const menuItems = document.querySelectorAll(".menu-item[data-view]");
    const views = document.querySelectorAll(".view");

    function openView(viewName) {
        menuItems.forEach(i => i.classList.toggle("active", i.dataset.view === viewName));
        views.forEach(v => v.classList.toggle("hidden", v.id !== `view-${viewName}`));

        if (viewName === "users") loadUsers();
        if (viewName === "dashboard") {
            loadDashboard().finally(() => initCharts()); // 切回首页刷新图表
        }
    }

    menuItems.forEach(i => i.addEventListener("click", () => openView(i.dataset.view)));

    // ========= Dashboard 数字卡 =========
    async function loadDashboard() {
        // 你后端如果有 /api/admin/stats，就会填数据；没有就不影响
        try {
            const stats = await request("/api/admin/stats");
            document.getElementById("statMovieCount").textContent = stats.movieCount ?? "--";
            document.getElementById("statCinemaCount").textContent = stats.cinemaCount ?? "--";
            document.getElementById("statTodayBox").textContent = stats.todayBox ?? "--";
            document.getElementById("statTotalBox").textContent = stats.totalBox ?? "--";
        } catch (_) {
            // 没接口也不报错
        }
    }

    // ========= 用户信息 + 搜索 + 授权 =========
    const tbody = document.getElementById("userTbody");
    const btnSearch = document.getElementById("btnSearch");
    const btnReload = document.getElementById("btnReload");
    const searchKey = document.getElementById("searchKey");

    btnSearch?.addEventListener("click", async () => {
        const key = (searchKey.value || "").trim();
        if (!key) return showHint("请输入 昵称#标识 或 用户名");
        showHint("搜索中…");
        try {
            const data = await request("/api/admin/users/search?key=" + encodeURIComponent(key));
            renderUsers(Array.isArray(data) ? data : [data]);
            showHint(`搜索完成：${Array.isArray(data) ? data.length : 1} 条`);
        } catch (e) {
            showHint(e.message);
        }
    });

    btnReload?.addEventListener("click", () => loadUsers());

    async function loadUsers() {
        showHint("加载用户列表…");
        try {
            const list = await request("/api/admin/users");
            renderUsers(list || []);
            showHint(`已加载：${(list || []).length} 个用户`);
        } catch (e) {
            showHint(e.message);
        }
    }

    function roleTag(role) {
        if (role === "ADMIN") return `<span class="tag tag-admin">ADMIN</span>`;
        return `<span class="tag tag-user">USER</span>`;
    }

    function nicknameDisplay(u) {
        if (u.nicknameDisplay) return u.nicknameDisplay;
        const n = u.nickname || u.username || "";
        const c = u.nicknameCode || u.nickname_code || "";
        return c ? `${n}#${c}` : n;
    }

    function renderUsers(users) {
        if (!tbody) return;

        // ✅ 授权页显示“注册过的用户信息”：至少要展示 username / nickname#code / role
        tbody.innerHTML = users.map(u => {
            const isAdmin = (u.role === "ADMIN");
            const btn = isAdmin
                ? `<button class="btn btn-ghost" data-act="revoke" data-id="${u.id}">取消授权</button>`
                : `<button class="btn" data-act="grant" data-id="${u.id}">授权为管理员</button>`;

            return `
        <tr>
          <td>${u.id}</td>
          <td>${u.username ?? ""}</td>
          <td>${nicknameDisplay(u)}</td>
          <td>${roleTag(u.role)}</td>
          <td>
            <div class="action-row">${btn}</div>
          </td>
        </tr>
      `;
        }).join("");

        tbody.querySelectorAll("button[data-act]").forEach(btn => {
            btn.addEventListener("click", async () => {
                const act = btn.getAttribute("data-act");
                const id = btn.getAttribute("data-id");
                if (!id) return;

                try {
                    btn.disabled = true;
                    if (act === "grant") {
                        showHint("授权中…");
                        await request(`/api/admin/users/${id}/grant-admin`, { method: "POST" });
                        showHint("授权成功");
                    } else if (act === "revoke") {
                        showHint("取消授权中…");
                        await request(`/api/admin/users/${id}/revoke-admin`, { method: "POST" });
                        showHint("已取消授权");
                    }
                    loadUsers();
                } catch (e) {
                    showHint(e.message);
                    btn.disabled = false;
                }
            });
        });
    }

    // ========= ECharts 图表 =========
    let chartLine, chartPie, chartBar;

    async function initCharts() {
        // 如果你没在 index.html 引入 echarts，这里会报错
        if (!window.echarts) return;

        const elLine = document.getElementById("chartLine");
        const elPie = document.getElementById("chartPie");
        const elBar = document.getElementById("chartBar");
        if (!elLine || !elPie || !elBar) return;

        // 避免重复 init
        if (!chartLine) chartLine = echarts.init(elLine);
        if (!chartPie) chartPie = echarts.init(elPie);
        if (!chartBar) chartBar = echarts.init(elBar);

        // 优先从后端拿数据（如果你还没有接口，会走 fallback）
        let data;
        try {
            // 你将来可以实现这个接口，返回如下结构即可：
            // {
            //   "weekDates": ["01-24","01-25",...],
            //   "weekBox": [0,10, ...],
            //   "typeMovieCount": [{"name":"动作","value":5}, ...],
            //   "typeBoxOffice": {"types":["动作","科幻"], "values":[1200,900]}
            // }
            data = await request("/api/admin/charts");
        } catch (_) {
            data = mockChartData();
        }

        // 折线
        chartLine.setOption({
            tooltip: { trigger: "axis" },
            grid: { left: 40, right: 20, top: 30, bottom: 30 },
            xAxis: { type: "category", data: data.weekDates },
            yAxis: { type: "value" },
            series: [{
                name: "票房(元)",
                type: "line",
                smooth: true,
                data: data.weekBox
            }]
        });

        // 饼图
        chartPie.setOption({
            tooltip: { trigger: "item" },
            legend: { top: 8, left: 8 },
            series: [{
                name: "电影数量",
                type: "pie",
                radius: ["35%", "65%"],
                center: ["50%", "58%"],
                data: data.typeMovieCount
            }]
        });

        // 柱图
        chartBar.setOption({
            tooltip: { trigger: "axis" },
            grid: { left: 50, right: 20, top: 40, bottom: 40 },
            xAxis: { type: "category", data: data.typeBoxOffice.types },
            yAxis: { type: "value" },
            series: [{
                name: "总票房(元)",
                type: "bar",
                data: data.typeBoxOffice.values
            }]
        });

        // 自适应
        window.addEventListener("resize", () => {
            chartLine?.resize();
            chartPie?.resize();
            chartBar?.resize();
        });
    }

    function mockChartData() {
        // 适合你现在“还没接后台图标/图表数据”的阶段：先能看到真实图表效果
        const weekDates = [];
        const weekBox = [];
        const now = new Date();
        for (let i = 6; i >= 0; i--) {
            const d = new Date(now.getTime() - i * 86400000);
            const mm = String(d.getMonth() + 1).padStart(2, "0");
            const dd = String(d.getDate()).padStart(2, "0");
            weekDates.push(`${mm}-${dd}`);
            weekBox.push(Math.floor(Math.random() * 200)); // 先用随机票房
        }

        const typeMovieCount = [
            { name: "纪录片", value: 2 },
            { name: "科幻", value: 4 },
            { name: "动作", value: 6 },
            { name: "喜剧", value: 3 },
            { name: "剧情", value: 5 }
        ];

        const typeBoxOffice = {
            types: ["纪录片", "科幻", "动作", "喜剧", "剧情"],
            values: [120, 880, 1320, 760, 980]
        };

        return { weekDates, weekBox, typeMovieCount, typeBoxOffice };
    }

    // ========= 初始化 =========
    initCollapsibleGroups();
    openView("dashboard");
})();
