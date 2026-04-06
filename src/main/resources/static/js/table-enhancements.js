(function () {
    const STYLE_ID = 'inventory-table-enhancements-style';
    const SORTABLE_CLASS = 'inventory-sortable-header';

    function ensureStyles() {
        if (document.getElementById(STYLE_ID)) {
            return;
        }

        const style = document.createElement('style');
        style.id = STYLE_ID;
        style.textContent = [
            '.inventory-table-toolbar {',
            '  display: flex;',
            '  justify-content: space-between;',
            '  align-items: center;',
            '  gap: 0.75rem;',
            '  flex-wrap: wrap;',
            '  margin: 1rem 0;',
            '  padding: 0.85rem 1rem;',
            '  border: 1px solid #d7e3f3;',
            '  border-radius: 0.5rem;',
            '  background: linear-gradient(135deg, #f8fbff 0%, #eef4ff 100%);',
            '}',
            '.inventory-table-toolbar-group {',
            '  display: flex;',
            '  align-items: center;',
            '  gap: 0.75rem;',
            '  flex-wrap: wrap;',
            '}',
            '.inventory-table-toolbar label {',
            '  margin-bottom: 0;',
            '  font-weight: 600;',
            '  color: #1f3a5f;',
            '}',
            '.inventory-table-toolbar select, .inventory-table-toolbar button {',
            '  min-height: 38px;',
            '}',
            '.inventory-table-summary {',
            '  color: #51647d;',
            '  font-size: 0.95rem;',
            '}',
            '.inventory-table-page-indicator {',
            '  min-width: 88px;',
            '  text-align: center;',
            '  font-weight: 600;',
            '  color: #1f3a5f;',
            '}',
            '.scrollable-container table[data-enhance-table="true"] thead th {',
            '  position: sticky;',
            '  top: 0;',
            '  z-index: 2;',
            '}',
            '.scrollable-container table[data-enhance-table="true"] .thead-dark th,',
            '.scrollable-container table[data-enhance-table="true"] thead.thead-dark th {',
            '  background-color: #343a40;',
            '  color: #fff;',
            '}',
            '.scrollable-container table[data-enhance-table="true"] thead th.' + SORTABLE_CLASS + ' {',
            '  cursor: pointer;',
            '  user-select: none;',
            '}',
            '.scrollable-container table[data-enhance-table="true"] thead th.' + SORTABLE_CLASS + '::after {',
            '  content: "\\2195";',
            '  margin-left: 0.4rem;',
            '  opacity: 0.45;',
            '  font-size: 0.8rem;',
            '}',
            '.scrollable-container table[data-enhance-table="true"] thead th[data-sort-direction="asc"]::after {',
            '  content: "\\2191";',
            '  opacity: 1;',
            '}',
            '.scrollable-container table[data-enhance-table="true"] thead th[data-sort-direction="desc"]::after {',
            '  content: "\\2193";',
            '  opacity: 1;',
            '}',
            '@media (max-width: 768px) {',
            '  .inventory-table-toolbar {',
            '    align-items: stretch;',
            '  }',
            '  .inventory-table-toolbar-group {',
            '    width: 100%;',
            '    justify-content: space-between;',
            '  }',
            '}',
        ].join('\n');

        document.head.appendChild(style);
    }

    function normalizeText(value) {
        return (value || '').replace(/\s+/g, ' ').trim();
    }

    function getCellValue(row, columnIndex) {
        const cell = row.cells[columnIndex];
        const rawText = cell ? normalizeText(cell.textContent) : '';
        const numericCandidate = rawText.replace(/,/g, '');

        if (numericCandidate && /^-?\d+(\.\d+)?$/.test(numericCandidate)) {
            return {
                type: 'number',
                value: Number(numericCandidate),
            };
        }

        return {
            type: 'string',
            value: rawText.toLowerCase(),
        };
    }

    function compareRows(leftRow, rightRow, columnIndex, direction) {
        const left = getCellValue(leftRow.row, columnIndex);
        const right = getCellValue(rightRow.row, columnIndex);

        let result = 0;
        if (left.type === 'number' && right.type === 'number') {
            result = left.value - right.value;
        } else {
            result = left.value.localeCompare(right.value, undefined, {
                numeric: true,
                sensitivity: 'base',
            });
        }

        if (result === 0) {
            result = leftRow.originalIndex - rightRow.originalIndex;
        }

        return direction === 'asc' ? result : result * -1;
    }

    function createToolbar(tableId) {
        const toolbar = document.createElement('div');
        toolbar.className = 'inventory-table-toolbar';
        toolbar.innerHTML = [
            '<div class="inventory-table-toolbar-group">',
            '  <label for="' + tableId + '-page-size">Rows</label>',
            '  <select class="form-control" id="' + tableId + '-page-size">',
            '    <option value="10">10</option>',
            '    <option value="25" selected>25</option>',
            '    <option value="50">50</option>',
            '    <option value="100">100</option>',
            '  </select>',
            '  <span class="inventory-table-summary"></span>',
            '</div>',
            '<div class="inventory-table-toolbar-group">',
            '  <button type="button" class="btn btn-outline-secondary inventory-table-prev">Previous</button>',
            '  <span class="inventory-table-page-indicator"></span>',
            '  <button type="button" class="btn btn-outline-secondary inventory-table-next">Next</button>',
            '</div>',
        ].join('');
        return toolbar;
    }

    function initializeTable(table, tableIndex) {
        if (!table || table.dataset.tableEnhancementsReady === 'true') {
            return;
        }

        const tbody = table.tBodies[0];
        const headerRow = table.tHead && table.tHead.rows.length ? table.tHead.rows[0] : null;
        if (!tbody || !headerRow) {
            return;
        }

        const rows = Array.from(tbody.rows).map(function (row, index) {
            return {
                row: row,
                originalIndex: index,
            };
        });
        if (!rows.length) {
            table.dataset.tableEnhancementsReady = 'true';
            return;
        }

        const scrollContainer = table.closest('.scrollable-container') || table.parentElement;
        if (!scrollContainer || !scrollContainer.parentNode) {
            return;
        }

        const tableId = table.id || 'inventory-table-' + tableIndex;
        table.id = tableId;
        table.dataset.enhanceTable = 'true';
        table.dataset.tableEnhancementsReady = 'true';

        const state = {
            rows: rows,
            page: 1,
            pageSize: 25,
            sortIndex: null,
            sortDirection: 'asc',
        };

        const toolbar = createToolbar(tableId);
        scrollContainer.parentNode.insertBefore(toolbar, scrollContainer);

        const pageSizeSelect = toolbar.querySelector('#' + tableId + '-page-size');
        const summary = toolbar.querySelector('.inventory-table-summary');
        const prevButton = toolbar.querySelector('.inventory-table-prev');
        const nextButton = toolbar.querySelector('.inventory-table-next');
        const pageIndicator = toolbar.querySelector('.inventory-table-page-indicator');
        const headers = Array.from(headerRow.cells);

        function updateHeaders() {
            headers.forEach(function (header, index) {
                if (!header.classList.contains(SORTABLE_CLASS)) {
                    return;
                }

                if (state.sortIndex === index) {
                    header.setAttribute('data-sort-direction', state.sortDirection);
                    header.setAttribute('aria-sort', state.sortDirection === 'asc' ? 'ascending' : 'descending');
                } else {
                    header.removeAttribute('data-sort-direction');
                    header.setAttribute('aria-sort', 'none');
                }
            });
        }

        function getOrderedRows() {
            const orderedRows = state.rows.slice();
            if (state.sortIndex === null) {
                orderedRows.sort(function (left, right) {
                    return left.originalIndex - right.originalIndex;
                });
                return orderedRows;
            }

            orderedRows.sort(function (left, right) {
                return compareRows(left, right, state.sortIndex, state.sortDirection);
            });
            return orderedRows;
        }

        function render() {
            const orderedRows = getOrderedRows();
            const totalRows = orderedRows.length;
            const totalPages = Math.max(1, Math.ceil(totalRows / state.pageSize));
            if (state.page > totalPages) {
                state.page = totalPages;
            }

            const startIndex = (state.page - 1) * state.pageSize;
            const endIndex = Math.min(startIndex + state.pageSize, totalRows);
            const fragment = document.createDocumentFragment();

            orderedRows.slice(startIndex, endIndex).forEach(function (entry) {
                fragment.appendChild(entry.row);
            });

            tbody.innerHTML = '';
            tbody.appendChild(fragment);

            const visibleStart = totalRows ? startIndex + 1 : 0;
            const visibleEnd = totalRows ? endIndex : 0;
            summary.textContent = 'Showing ' + visibleStart + '-' + visibleEnd + ' of ' + totalRows;
            pageIndicator.textContent = 'Page ' + state.page + ' / ' + totalPages;
            prevButton.disabled = state.page <= 1;
            nextButton.disabled = state.page >= totalPages;
            updateHeaders();
        }

        headers.forEach(function (header, index) {
            const label = normalizeText(header.textContent);
            const isSortable = label && !/actions?/i.test(label) && header.dataset.sort !== 'none';
            if (!isSortable) {
                return;
            }

            header.classList.add(SORTABLE_CLASS);
            header.tabIndex = 0;
            header.setAttribute('role', 'button');
            header.setAttribute('aria-sort', 'none');

            function toggleSort() {
                if (state.sortIndex === index) {
                    state.sortDirection = state.sortDirection === 'asc' ? 'desc' : 'asc';
                } else {
                    state.sortIndex = index;
                    state.sortDirection = 'asc';
                }
                state.page = 1;
                render();
            }

            header.addEventListener('click', toggleSort);
            header.addEventListener('keydown', function (event) {
                if (event.key === 'Enter' || event.key === ' ') {
                    event.preventDefault();
                    toggleSort();
                }
            });
        });

        pageSizeSelect.addEventListener('change', function () {
            state.pageSize = Number(pageSizeSelect.value) || 25;
            state.page = 1;
            render();
        });

        prevButton.addEventListener('click', function () {
            if (state.page > 1) {
                state.page -= 1;
                render();
            }
        });

        nextButton.addEventListener('click', function () {
            const totalPages = Math.max(1, Math.ceil(state.rows.length / state.pageSize));
            if (state.page < totalPages) {
                state.page += 1;
                render();
            }
        });

        render();
    }

    function init(selector) {
        ensureStyles();
        const tables = Array.from(document.querySelectorAll(selector || '[data-enhance-table="true"]'));
        tables.forEach(function (table, index) {
            initializeTable(table, index + 1);
        });
    }

    window.InventoryTableEnhancer = {
        init: init,
    };

    document.addEventListener('DOMContentLoaded', function () {
        init();
    });
}());
