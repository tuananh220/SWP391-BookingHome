/**
 * Address Selector Component for Vietnam Administrative Units (2025/2026 - 34 Provinces/Cities)
 * Automatically loads Provinces and Wards/Communes dynamically.
 */
(function (window) {
    'use strict';

    // Global in-memory cache on client to avoid redundant network requests across forms/interactions
    const provinceCache = { data: null };
    const wardCache = new Map();

    function initAddressSelector(options) {
        options = options || {};
        const provinceSelect = document.getElementById(options.provinceSelectId || 'provinceSelect');
        const wardSelect = document.getElementById(options.wardSelectId || 'wardSelect');
        const contextPath = options.contextPath || '';
        const initialCity = (options.initialCity || '').trim();
        const initialDistrict = (options.initialDistrict || '').trim();

        if (!provinceSelect || !wardSelect) {
            return;
        }

        let isInitialLoad = true;

        // Fetch provinces with cache & fallback
        async function fetchProvinces() {
            if (provinceCache.data && Array.isArray(provinceCache.data) && provinceCache.data.length > 0) {
                return provinceCache.data;
            }

            // 1. Try local server endpoint first
            try {
                const response = await fetch(contextPath + '/api/provinces');
                if (response.ok) {
                    const data = await response.json();
                    if (Array.isArray(data) && data.length > 0) {
                        provinceCache.data = data;
                        return data;
                    }
                }
            } catch (err) {
                console.warn('Local province API endpoint unreachable, attempting fallback to direct open-api.vn v2...', err);
            }

            // 2. Direct public API v2 fallback
            try {
                const fallbackResponse = await fetch('https://provinces.open-api.vn/api/v2/p/');
                if (fallbackResponse.ok) {
                    const data = await fallbackResponse.json();
                    if (Array.isArray(data) && data.length > 0) {
                        provinceCache.data = data;
                        return data;
                    }
                }
            } catch (err) {
                console.error('Failed to fetch provinces from direct API fallback as well:', err);
            }

            return [];
        }

        // Fetch wards with cache & fallback
        async function fetchWards(provinceCode) {
            if (wardCache.has(provinceCode)) {
                return wardCache.get(provinceCode);
            }

            // 1. Try local server endpoint
            try {
                const response = await fetch(contextPath + '/api/provinces?provinceCode=' + encodeURIComponent(provinceCode));
                if (response.ok) {
                    const data = await response.json();
                    if (Array.isArray(data) && data.length > 0) {
                        wardCache.set(provinceCode, data);
                        return data;
                    }
                }
            } catch (err) {
                console.warn('Local wards API endpoint unreachable, attempting fallback to direct open-api.vn v2...', err);
            }

            // 2. Direct public API v2 fallback
            try {
                const fallbackResponse = await fetch('https://provinces.open-api.vn/api/v2/w/?province=' + encodeURIComponent(provinceCode));
                if (fallbackResponse.ok) {
                    const data = await fallbackResponse.json();
                    if (Array.isArray(data)) {
                        wardCache.set(provinceCode, data);
                        return data;
                    }
                }
            } catch (err) {
                console.error('Failed to fetch wards from direct API fallback as well:', err);
            }

            return [];
        }

        async function loadWardsForProvince(provinceCode, targetDistrictToSelect) {
            wardSelect.innerHTML = '<option value="">-- Đang tải danh sách Phường / Xã... --</option>';
            wardSelect.disabled = true;

            if (!provinceCode) {
                wardSelect.innerHTML = '<option value="">-- Vui lòng chọn Tỉnh / Thành phố trước --</option>';
                wardSelect.disabled = false;
                return;
            }

            try {
                const wards = await fetchWards(provinceCode);
                wardSelect.innerHTML = '<option value="">-- Chọn Phường / Xã / Đơn vị hành chính --</option>';

                let matched = false;
                if (Array.isArray(wards) && wards.length > 0) {
                    wards.forEach(function (ward) {
                        const opt = document.createElement('option');
                        opt.value = ward.name;
                        opt.textContent = ward.name;
                        opt.setAttribute('data-code', ward.code);

                        if (targetDistrictToSelect && ward.name.trim().toLowerCase() === targetDistrictToSelect.toLowerCase()) {
                            opt.selected = true;
                            matched = true;
                        }
                        wardSelect.appendChild(opt);
                    });
                }

                // If target district was specified (from existing homestay data) but not found in API list, append it as custom/legacy option
                if (targetDistrictToSelect && !matched) {
                    const customOpt = document.createElement('option');
                    customOpt.value = targetDistrictToSelect;
                    customOpt.textContent = targetDistrictToSelect + ' (Hiện tại)';
                    customOpt.selected = true;
                    wardSelect.appendChild(customOpt);
                }
            } catch (error) {
                console.error('Error in loadWardsForProvince:', error);
                wardSelect.innerHTML = '<option value="">-- Lỗi tải danh sách Phường / Xã --</option>';
            } finally {
                wardSelect.disabled = false;
            }
        }

        async function init() {
            provinceSelect.innerHTML = '<option value="">-- Đang tải danh sách Tỉnh / Thành phố... --</option>';
            provinceSelect.disabled = true;

            try {
                const provinces = await fetchProvinces();
                provinceSelect.innerHTML = '<option value="">-- Chọn Tỉnh / Thành phố --</option>';

                let selectedProvinceCode = null;
                if (Array.isArray(provinces) && provinces.length > 0) {
                    provinces.forEach(function (province) {
                        const opt = document.createElement('option');
                        opt.value = province.name;
                        opt.textContent = province.name;
                        opt.setAttribute('data-code', province.code);

                        if (initialCity && province.name.trim().toLowerCase() === initialCity.toLowerCase()) {
                            opt.selected = true;
                            selectedProvinceCode = province.code;
                        }
                        provinceSelect.appendChild(opt);
                    });
                }

                // If initial city is custom or not in 34 provinces list, append it
                if (initialCity && !selectedProvinceCode) {
                    const customProvOpt = document.createElement('option');
                    customProvOpt.value = initialCity;
                    customProvOpt.textContent = initialCity + ' (Hiện tại)';
                    customProvOpt.selected = true;
                    provinceSelect.appendChild(customProvOpt);
                }

                provinceSelect.disabled = false;

                // Load wards for selected province if present
                if (selectedProvinceCode) {
                    await loadWardsForProvince(selectedProvinceCode, initialDistrict);
                } else if (initialDistrict) {
                    wardSelect.innerHTML = '';
                    const customWardOpt = document.createElement('option');
                    customWardOpt.value = initialDistrict;
                    customWardOpt.textContent = initialDistrict;
                    customWardOpt.selected = true;
                    wardSelect.appendChild(customWardOpt);
                } else {
                    wardSelect.innerHTML = '<option value="">-- Vui lòng chọn Tỉnh / Thành phố trước --</option>';
                }
            } catch (error) {
                console.error('Error in init address selector:', error);
                provinceSelect.innerHTML = '<option value="">-- Lỗi kết nối API Tỉnh / Thành --</option>';
                provinceSelect.disabled = false;
            }
        }

        provinceSelect.addEventListener('change', function () {
            const selectedOption = provinceSelect.options[provinceSelect.selectedIndex];
            const provinceCode = selectedOption ? selectedOption.getAttribute('data-code') : null;
            loadWardsForProvince(provinceCode, isInitialLoad ? initialDistrict : '');
            isInitialLoad = false;
        });

        init();
    }

    window.initAddressSelector = initAddressSelector;
})(window);
