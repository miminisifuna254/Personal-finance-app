// ─────────────────────────────────────────────────────────────────────────────
// FinanceOS — Personal Finance Tracker
// Single self-contained React component
// Stack: React + Tailwind CSS + recharts + lucide-react
// State: in-memory only (useState / useReducer)
// ─────────────────────────────────────────────────────────────────────────────

import React, { useState, useReducer, useMemo } from 'react';
import {
  PlusCircle,
  Trash2,
  TrendingUp,
  LayoutDashboard,
  Wallet,
  AlertCircle,
  CheckCircle,
  Calendar,
  PieChart as PieChartIcon,
} from 'lucide-react';
import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  Legend,
  ResponsiveContainer,
  PieChart,
  Pie,
  Cell,
  AreaChart,
  Area,
} from 'recharts';

// ─── Palette ─────────────────────────────────────────────────────────────────
const PALETTE = [
  '#c9a84c', // gold
  '#4ade80', // green
  '#60a5fa', // blue
  '#f472b6', // pink
  '#a78bfa', // purple
  '#fb923c', // orange
  '#34d399', // teal
];

// ─── Date helpers ─────────────────────────────────────────────────────────────
const today        = new Date();
const CURRENT_YEAR  = today.getFullYear();
const CURRENT_MONTH = today.getMonth(); // 0-indexed
const DAYS_IN_MONTH = new Date(CURRENT_YEAR, CURRENT_MONTH + 1, 0).getDate();

/** Return ISO date string for a given day of the current month */
const d = (day) =>
  new Date(CURRENT_YEAR, CURRENT_MONTH, day).toISOString().split('T')[0];

/** Format a number as "KES X,XXX" */
const fmt = (n) =>
  `KES ${Number(n).toLocaleString('en-KE', { maximumFractionDigits: 0 })}`;

// ─── Seed Data ────────────────────────────────────────────────────────────────

const SEED_CATEGORIES = [
  { id: 1, name: 'Housing',       budget: 45000 },
  { id: 2, name: 'Food & Dining', budget: 15000 },
  { id: 3, name: 'Transport',     budget: 8000  },
  { id: 4, name: 'Entertainment', budget: 5000  },
  { id: 5, name: 'Health',        budget: 6000  },
];

const SEED_EXPENSES = [
  { id: 1,  description: 'Monthly Rent',          amount: 42000, categoryId: 1, date: d(1)  },
  { id: 2,  description: 'Naivas Supermarket',    amount: 4500,  categoryId: 2, date: d(3)  },
  { id: 3,  description: 'Uber to CBD',           amount: 850,   categoryId: 3, date: d(4)  },
  { id: 4,  description: 'Netflix Subscription',  amount: 1100,  categoryId: 4, date: d(5)  },
  { id: 5,  description: 'Pharmacy — Vitamins',   amount: 2200,  categoryId: 5, date: d(6)  },
  { id: 6,  description: 'Quickmart Grocery',     amount: 3800,  categoryId: 2, date: d(8)  },
  { id: 7,  description: 'Matatu Fares (Week)',   amount: 2100,  categoryId: 3, date: d(10) },
  { id: 8,  description: 'Java House — Dinner',   amount: 2400,  categoryId: 2, date: d(12) },
  { id: 9,  description: 'Cinema (2 Tickets)',    amount: 1800,  categoryId: 4, date: d(14) },
  { id: 10, description: 'Gym Membership',        amount: 3500,  categoryId: 5, date: d(15) },
  { id: 11, description: 'Total Petrol — Fuel',   amount: 3200,  categoryId: 3, date: d(17) },
  { id: 12, description: 'Spotify + Apple Music', amount: 1600,  categoryId: 4, date: d(19) },
];

// ─── Reducer ─────────────────────────────────────────────────────────────────

const initialState = {
  categories: SEED_CATEGORIES,
  expenses:   SEED_EXPENSES,
};

function financeReducer(state, action) {
  switch (action.type) {
    case 'ADD_CATEGORY':
      return { ...state, categories: [...state.categories, action.payload] };

    case 'REMOVE_CATEGORY':
      return {
        ...state,
        // Remove the category and all expenses that reference it
        categories: state.categories.filter((c) => c.id !== action.payload),
        expenses:   state.expenses.filter((e) => e.categoryId !== action.payload),
      };

    case 'ADD_EXPENSE':
      return { ...state, expenses: [...state.expenses, action.payload] };

    case 'REMOVE_EXPENSE':
      return { ...state, expenses: state.expenses.filter((e) => e.id !== action.payload) };

    default:
      return state;
  }
}

// ─────────────────────────────────────────────────────────────────────────────
// Main Component
// ─────────────────────────────────────────────────────────────────────────────

export default function FinanceOS() {
  // ── Navigation
  const [activeTab, setActiveTab] = useState('budget');

  // ── Core state
  const [state, dispatch] = useReducer(financeReducer, initialState);

  // ── New category form
  const [newCat, setNewCat] = useState({ name: '', budget: '' });

  // ── New expense form
  const [newExp, setNewExp] = useState({
    description: '',
    amount:      '',
    categoryId:  '',
    date:        today.toISOString().split('T')[0],
  });

  // ─── Derived: total spent per category ─────────────────────────────────────
  const catSpend = useMemo(() => {
    const map = {};
    state.categories.forEach((c) => { map[c.id] = 0; });
    state.expenses.forEach((e) => {
      if (map[e.categoryId] !== undefined) map[e.categoryId] += e.amount;
    });
    return map;
  }, [state.expenses, state.categories]);

  // ─── Totals ────────────────────────────────────────────────────────────────
  const totalBudget = useMemo(
    () => state.categories.reduce((s, c) => s + c.budget, 0),
    [state.categories]
  );
  const totalSpent = useMemo(
    () => Object.values(catSpend).reduce((s, v) => s + v, 0),
    [catSpend]
  );
  const totalLeft = totalBudget - totalSpent;
  const pctUsed   = totalBudget > 0
    ? Math.min(Math.round((totalSpent / totalBudget) * 100), 999)
    : 0;

  // ─── Health score: % of categories within budget (0–100) ──────────────────
  const healthScore = useMemo(() => {
    if (!state.categories.length) return 100;
    const ok = state.categories.filter((c) => (catSpend[c.id] || 0) <= c.budget).length;
    return Math.round((ok / state.categories.length) * 100);
  }, [state.categories, catSpend]);

  // ─── Chart: grouped bar (Budget vs Actual per category) ───────────────────
  const barData = useMemo(() =>
    state.categories.map((c) => ({
      name:   c.name.length > 11 ? c.name.slice(0, 11) + '…' : c.name,
      Budget: c.budget,
      Actual: catSpend[c.id] || 0,
    })),
    [state.categories, catSpend]
  );

  // ─── Chart: donut (spending distribution) ─────────────────────────────────
  const pieData = useMemo(() =>
    state.categories
      .map((c, i) => ({
        name:  c.name,
        value: catSpend[c.id] || 0,
        color: PALETTE[i % PALETTE.length],
      }))
      .filter((d) => d.value > 0),
    [state.categories, catSpend]
  );

  // ─── Chart: cumulative monthly spending trend ──────────────────────────────
  const trendData = useMemo(() => {
    const dayMap = {};
    state.expenses.forEach((e) => {
      const day = parseInt(e.date.split('-')[2], 10);
      if (!isNaN(day)) dayMap[day] = (dayMap[day] || 0) + e.amount;
    });
    let cumul = 0;
    const data = [];
    for (let day = 1; day <= today.getDate(); day++) {
      cumul += dayMap[day] || 0;
      data.push({ day: String(day), spent: cumul });
    }
    return data;
  }, [state.expenses]);

  // ─── Insights ─────────────────────────────────────────────────────────────
  const insights = useMemo(() => {
    const overBudget = state.categories
      .map((c) => ({ name: c.name, over: (catSpend[c.id] || 0) - c.budget }))
      .filter((c) => c.over > 0)
      .sort((a, b) => b.over - a.over);

    const byEfficiency = state.categories
      .map((c) => ({
        name:  c.name,
        ratio: c.budget > 0 ? (catSpend[c.id] || 0) / c.budget : 1,
      }))
      .sort((a, b) => a.ratio - b.ratio);

    return {
      highestOverspend: overBudget[0]?.name    ?? 'None',
      mostEfficient:    byEfficiency[0]?.name  ?? 'None',
      daysRemaining:    DAYS_IN_MONTH - today.getDate(),
    };
  }, [state.categories, catSpend]);

  // ─── Expenses grouped by category for the log view ────────────────────────
  const groupedExpenses = useMemo(() => {
    const map = {};
    state.categories.forEach((c) => { map[c.id] = { category: c, expenses: [] }; });
    state.expenses.forEach((e) => {
      if (map[e.categoryId]) map[e.categoryId].expenses.push(e);
    });
    return Object.values(map).filter((g) => g.expenses.length > 0);
  }, [state.categories, state.expenses]);

  // ─── Handlers ─────────────────────────────────────────────────────────────
  const addCategory = () => {
    if (!newCat.name.trim() || !newCat.budget) return;
    dispatch({
      type:    'ADD_CATEGORY',
      payload: {
        id:     Date.now(),
        name:   newCat.name.trim(),
        budget: parseFloat(newCat.budget),
      },
    });
    setNewCat({ name: '', budget: '' });
  };

  const addExpense = () => {
    if (!newExp.description.trim() || !newExp.amount || !newExp.categoryId) return;
    dispatch({
      type:    'ADD_EXPENSE',
      payload: {
        id:          Date.now(),
        description: newExp.description.trim(),
        amount:      parseFloat(newExp.amount),
        categoryId:  parseInt(newExp.categoryId, 10),
        date:        newExp.date,
      },
    });
    setNewExp({
      description: '',
      amount:      '',
      categoryId:  '',
      date:        today.toISOString().split('T')[0],
    });
  };

  // ─── Shared style helpers ─────────────────────────────────────────────────

  /** Reusable input class */
  const inputCls =
    'w-full bg-white/5 border border-white/10 rounded-lg px-3.5 py-2.5 text-white text-sm ' +
    'placeholder-white/20 focus:outline-none focus:border-[#c9a84c]/50 transition-colors';

  // ─────────────────────────────────────────────────────────────────────────
  // RENDER
  // ─────────────────────────────────────────────────────────────────────────
  return (
    <div className="min-h-screen bg-[#0f0f0f] text-white">

      {/* ── Google Fonts + micro-animations ─────────────────────────────── */}
      <style>{`
        @import url('https://fonts.googleapis.com/css2?family=DM+Serif+Display:ital@0;1&family=JetBrains+Mono:wght@300;400;500;600&display=swap');

        /* Utility classes Tailwind can't reach */
        .ds  { font-family: 'DM Serif Display', Georgia, serif; }
        .jb  { font-family: 'JetBrains Mono', 'Courier New', monospace; }

        /* Tab fade-in */
        @keyframes fadeUp {
          from { opacity: 0; transform: translateY(10px); }
          to   { opacity: 1; transform: translateY(0);    }
        }
        .fade-up { animation: fadeUp 0.35s ease both; }

        /* Staggered card entrance */
        .card-in { animation: fadeUp 0.4s ease both; }

        /* Progress bar fill */
        .bar-fill { transition: width 0.75s cubic-bezier(0.4, 0, 0.2, 1); }

        /* Date picker icon */
        input[type="date"]::-webkit-calendar-picker-indicator {
          filter: invert(0.45);
          cursor: pointer;
        }

        /* Custom scrollbar */
        ::-webkit-scrollbar       { width: 3px; height: 3px; }
        ::-webkit-scrollbar-track { background: #1a1a1a; }
        ::-webkit-scrollbar-thumb { background: #c9a84c66; border-radius: 2px; }
      `}</style>

      {/* ══════════════════════════════════════════════════════════════════════
          HEADER / NAV
      ══════════════════════════════════════════════════════════════════════ */}
      <header className="sticky top-0 z-50 bg-[#0f0f0f]/90 backdrop-blur-md border-b border-white/[0.06]">
        <div className="max-w-5xl mx-auto px-4 sm:px-6 h-16 flex items-center justify-between gap-4">

          {/* Logo */}
          <div className="flex items-center gap-2.5 shrink-0">
            <Wallet size={20} className="text-[#c9a84c]" />
            <span className="ds text-xl tracking-tight">FinanceOS</span>
          </div>

          {/* Tab Navigation */}
          <nav className="flex gap-1 bg-[#1a1a1a] rounded-lg p-1">
            {[
              { id: 'budget',   label: 'Budget Setup', Icon: LayoutDashboard },
              { id: 'expenses', label: 'Expenses',     Icon: Wallet           },
              { id: 'reports',  label: 'Reports',      Icon: TrendingUp       },
            ].map(({ id, label, Icon }) => (
              <button
                key={id}
                onClick={() => setActiveTab(id)}
                className={[
                  'flex items-center gap-1.5 px-3 sm:px-4 py-2 rounded-md',
                  'text-xs sm:text-sm transition-all duration-200 font-medium',
                  activeTab === id
                    ? 'bg-[#c9a84c] text-black'
                    : 'text-white/50 hover:text-white/80',
                ].join(' ')}
              >
                <Icon size={14} />
                <span className="hidden sm:inline">{label}</span>
              </button>
            ))}
          </nav>
        </div>
      </header>

      {/* ══════════════════════════════════════════════════════════════════════
          MAIN CONTENT
      ══════════════════════════════════════════════════════════════════════ */}
      <main className="max-w-5xl mx-auto px-4 sm:px-6 py-8 space-y-6">

        {/* ══════════════════════════════════════════════════════════════════
            TAB 1 — BUDGET SETUP + LIVE DASHBOARD
        ══════════════════════════════════════════════════════════════════ */}
        {activeTab === 'budget' && (
          <div className="space-y-6 fade-up">

            {/* ── Summary Cards ── */}
            <div className="grid grid-cols-2 lg:grid-cols-4 gap-3">
              {[
                {
                  label: 'Total Budget',
                  value: fmt(totalBudget),
                  sub:   'Monthly allocation',
                  color: '#c9a84c',
                  delay: '0s',
                },
                {
                  label: 'Total Spent',
                  value: fmt(totalSpent),
                  sub:   `${pctUsed}% of budget used`,
                  color: totalSpent > totalBudget ? '#f87171' : '#4ade80',
                  delay: '0.07s',
                },
                {
                  label: 'Remaining',
                  value: fmt(Math.abs(totalLeft)),
                  sub:   totalLeft < 0 ? '⚠ Over budget' : 'Available',
                  color: totalLeft < 0 ? '#f87171' : '#4ade80',
                  delay: '0.14s',
                },
                {
                  label: 'Health Score',
                  value: `${healthScore} / 100`,
                  sub:   healthScore >= 80 ? 'Excellent' : healthScore >= 50 ? 'Fair' : 'Needs attention',
                  color: healthScore >= 80 ? '#4ade80' : healthScore >= 50 ? '#c9a84c' : '#f87171',
                  delay: '0.21s',
                },
              ].map((card) => (
                <div
                  key={card.label}
                  className="card-in bg-[#1a1a1a] rounded-xl p-4 border border-white/[0.06] hover:border-[#c9a84c]/20 transition-colors"
                  style={{ animationDelay: card.delay }}
                >
                  <p className="text-white/40 text-[10px] uppercase tracking-[0.14em] mb-2">
                    {card.label}
                  </p>
                  <p className="jb text-xl font-medium leading-none" style={{ color: card.color }}>
                    {card.value}
                  </p>
                  <p className="text-white/30 text-[11px] mt-1.5">{card.sub}</p>
                </div>
              ))}
            </div>

            {/* ── Per-Category Budget Overview ── */}
            <div className="bg-[#1a1a1a] rounded-xl border border-white/[0.06]">
              <div className="px-5 py-4 border-b border-white/[0.06] flex items-center justify-between">
                <h2 className="ds text-xl">Budget Overview</h2>
                <span className="jb text-[#c9a84c] text-xs">{state.categories.length} categories</span>
              </div>

              {state.categories.length === 0 ? (
                <div className="px-5 py-12 text-center text-white/30 text-sm">
                  No categories yet — add one below.
                </div>
              ) : (
                <div className="divide-y divide-white/[0.04]">
                  {state.categories.map((cat, i) => {
                    const spent     = catSpend[cat.id] || 0;
                    const remaining = cat.budget - spent;
                    const pct       = cat.budget > 0
                      ? Math.min((spent / cat.budget) * 100, 100)
                      : 0;
                    const isOver    = spent > cat.budget;
                    const barColor  = isOver ? '#f87171' : PALETTE[i % PALETTE.length];

                    return (
                      <div key={cat.id} className="px-5 py-4">

                        {/* Row header */}
                        <div className="flex items-center justify-between mb-2.5">
                          <div className="flex items-center gap-2">
                            <div
                              className="w-2 h-2 rounded-full shrink-0"
                              style={{ backgroundColor: PALETTE[i % PALETTE.length] }}
                            />
                            <span className="text-white text-sm font-medium">{cat.name}</span>
                          </div>
                          <span
                            className="text-[11px] font-medium px-2 py-0.5 rounded-full jb"
                            style={{
                              background: isOver ? '#f8717120' : '#4ade8020',
                              color:      isOver ? '#f87171'   : '#4ade80',
                            }}
                          >
                            {isOver
                              ? `▲ ${fmt(Math.abs(remaining))} over`
                              : `✓ ${fmt(remaining)} left`}
                          </span>
                        </div>

                        {/* Progress bar */}
                        <div className="h-1 bg-white/10 rounded-full overflow-hidden mb-3">
                          <div
                            className="h-full rounded-full bar-fill"
                            style={{ width: `${pct}%`, backgroundColor: barColor }}
                          />
                        </div>

                        {/* Number row */}
                        <div className="flex flex-wrap gap-x-5 gap-y-0.5 text-xs jb">
                          <span className="text-white/40">
                            Budget{' '}
                            <span className="text-[#c9a84c]">{fmt(cat.budget)}</span>
                          </span>
                          <span className="text-white/40">
                            Spent{' '}
                            <span className="text-white">{fmt(spent)}</span>
                          </span>
                          <span className="text-white/40">
                            {pct.toFixed(0)}% used
                          </span>
                        </div>
                      </div>
                    );
                  })}
                </div>
              )}
            </div>

            {/* ── Budget Builder: Add / Remove Categories ── */}
            <div className="bg-[#1a1a1a] rounded-xl border border-white/[0.06] p-5 space-y-4">
              <h2 className="ds text-xl">Manage Categories</h2>

              {/* Add row */}
              <div className="flex gap-2">
                <input
                  type="text"
                  value={newCat.name}
                  onChange={(e) => setNewCat((p) => ({ ...p, name: e.target.value }))}
                  onKeyDown={(e) => e.key === 'Enter' && addCategory()}
                  placeholder="Category name"
                  className={inputCls + ' flex-1'}
                />
                <input
                  type="number"
                  value={newCat.budget}
                  onChange={(e) => setNewCat((p) => ({ ...p, budget: e.target.value }))}
                  onKeyDown={(e) => e.key === 'Enter' && addCategory()}
                  placeholder="Budget (KES)"
                  min="0"
                  className={inputCls + ' w-32 sm:w-40 jb'}
                />
                <button
                  onClick={addCategory}
                  title="Add category"
                  className="bg-[#c9a84c] hover:bg-[#d4b460] active:scale-95 text-black p-2.5 rounded-lg transition-all shrink-0"
                >
                  <PlusCircle size={18} />
                </button>
              </div>

              {/* Category list */}
              <div className="space-y-1.5 max-h-64 overflow-y-auto pr-0.5">
                {state.categories.map((cat, i) => (
                  <div
                    key={cat.id}
                    className="flex items-center justify-between bg-white/[0.03] hover:bg-white/[0.06] rounded-lg px-4 py-2.5 transition-colors"
                  >
                    <div className="flex items-center gap-2.5">
                      <div
                        className="w-2 h-2 rounded-full shrink-0"
                        style={{ backgroundColor: PALETTE[i % PALETTE.length] }}
                      />
                      <span className="text-white text-sm">{cat.name}</span>
                    </div>
                    <div className="flex items-center gap-3">
                      <span className="jb text-[#c9a84c] text-sm">{fmt(cat.budget)}</span>
                      <button
                        onClick={() => dispatch({ type: 'REMOVE_CATEGORY', payload: cat.id })}
                        title="Remove category"
                        className="text-white/20 hover:text-[#f87171] transition-colors"
                      >
                        <Trash2 size={14} />
                      </button>
                    </div>
                  </div>
                ))}
                {state.categories.length === 0 && (
                  <p className="text-white/30 text-sm text-center py-4">No categories yet.</p>
                )}
              </div>
            </div>
          </div>
        )}

        {/* ══════════════════════════════════════════════════════════════════
            TAB 2 — EXPENSE ENTRY + LOG
        ══════════════════════════════════════════════════════════════════ */}
        {activeTab === 'expenses' && (
          <div className="space-y-6 fade-up">
            <div className="grid lg:grid-cols-5 gap-6">

              {/* ── Add Expense Form ── */}
              <div className="lg:col-span-2 bg-[#1a1a1a] rounded-xl border border-white/[0.06] p-5 space-y-4 h-fit">
                <h2 className="ds text-xl">Add Expense</h2>

                {/* Description */}
                <div className="space-y-1.5">
                  <label className="text-white/40 text-[10px] uppercase tracking-[0.14em]">
                    Description
                  </label>
                  <input
                    type="text"
                    value={newExp.description}
                    onChange={(e) => setNewExp((p) => ({ ...p, description: e.target.value }))}
                    placeholder="e.g. Naivas Supermarket"
                    className={inputCls}
                  />
                </div>

                {/* Amount */}
                <div className="space-y-1.5">
                  <label className="text-white/40 text-[10px] uppercase tracking-[0.14em]">
                    Amount (KES)
                  </label>
                  <input
                    type="number"
                    value={newExp.amount}
                    onChange={(e) => setNewExp((p) => ({ ...p, amount: e.target.value }))}
                    placeholder="0"
                    min="0"
                    className={inputCls + ' jb'}
                  />
                </div>

                {/* Category */}
                <div className="space-y-1.5">
                  <label className="text-white/40 text-[10px] uppercase tracking-[0.14em]">
                    Category
                  </label>
                  <select
                    value={newExp.categoryId}
                    onChange={(e) => setNewExp((p) => ({ ...p, categoryId: e.target.value }))}
                    className="w-full bg-[#0f0f0f] border border-white/10 rounded-lg px-3.5 py-2.5 text-white text-sm focus:outline-none focus:border-[#c9a84c]/50 transition-colors"
                  >
                    <option value="">Select a category</option>
                    {state.categories.map((c) => (
                      <option key={c.id} value={c.id}>{c.name}</option>
                    ))}
                  </select>
                </div>

                {/* Date */}
                <div className="space-y-1.5">
                  <label className="text-white/40 text-[10px] uppercase tracking-[0.14em]">
                    Date
                  </label>
                  <input
                    type="date"
                    value={newExp.date}
                    onChange={(e) => setNewExp((p) => ({ ...p, date: e.target.value }))}
                    className={inputCls}
                  />
                </div>

                <button
                  onClick={addExpense}
                  className="w-full bg-[#c9a84c] hover:bg-[#d4b460] active:scale-[0.98] text-black font-semibold py-2.5 rounded-lg flex items-center justify-center gap-2 transition-all text-sm"
                >
                  <PlusCircle size={16} />
                  Add Expense
                </button>
              </div>

              {/* ── Expense Log ── */}
              <div className="lg:col-span-3 bg-[#1a1a1a] rounded-xl border border-white/[0.06] flex flex-col">
                <div className="px-5 py-4 border-b border-white/[0.06] flex items-center justify-between shrink-0">
                  <h2 className="ds text-xl">Expense Log</h2>
                  <span className="jb text-[#c9a84c] text-xs">{state.expenses.length} entries</span>
                </div>

                <div className="overflow-y-auto max-h-[520px] divide-y divide-white/[0.04]">
                  {groupedExpenses.length === 0 ? (
                    <div className="px-5 py-12 text-center text-white/30 text-sm">
                      No expenses yet. Add your first one!
                    </div>
                  ) : (
                    groupedExpenses.map(({ category, expenses: exps }, gi) => (
                      <div key={category.id}>

                        {/* Group header */}
                        <div className="px-5 py-2 flex items-center gap-2 bg-white/[0.015] sticky top-0">
                          <div
                            className="w-1.5 h-1.5 rounded-full shrink-0"
                            style={{ backgroundColor: PALETTE[gi % PALETTE.length] }}
                          />
                          <span className="text-white/40 text-[10px] uppercase tracking-[0.14em]">
                            {category.name}
                          </span>
                          <span className="jb text-white/25 text-[11px] ml-auto">
                            {fmt(exps.reduce((s, e) => s + e.amount, 0))}
                          </span>
                        </div>

                        {/* Expense rows */}
                        {exps
                          .slice()
                          .sort((a, b) => new Date(b.date) - new Date(a.date))
                          .map((exp) => (
                            <div
                              key={exp.id}
                              className="px-5 py-3 flex items-center justify-between hover:bg-white/[0.025] transition-colors group"
                            >
                              <div>
                                <p className="text-white text-sm">{exp.description}</p>
                                <p className="text-white/30 text-[11px] jb mt-0.5">{exp.date}</p>
                              </div>
                              <div className="flex items-center gap-4">
                                <span className="jb text-white text-sm">{fmt(exp.amount)}</span>
                                <button
                                  onClick={() =>
                                    dispatch({ type: 'REMOVE_EXPENSE', payload: exp.id })
                                  }
                                  title="Delete expense"
                                  className="text-white/15 hover:text-[#f87171] transition-colors opacity-0 group-hover:opacity-100"
                                >
                                  <Trash2 size={14} />
                                </button>
                              </div>
                            </div>
                          ))}
                      </div>
                    ))
                  )}
                </div>
              </div>
            </div>
          </div>
        )}

        {/* ══════════════════════════════════════════════════════════════════
            TAB 3 — REPORTS
        ══════════════════════════════════════════════════════════════════ */}
        {activeTab === 'reports' && (
          <div className="space-y-6 fade-up">

            {/* ── Key Insights ── */}
            <div className="grid sm:grid-cols-3 gap-3">
              {[
                {
                  label: 'Highest Overspend',
                  value: insights.highestOverspend,
                  Icon:  AlertCircle,
                  color: '#f87171',
                  delay: '0s',
                },
                {
                  label: 'Most Efficient',
                  value: insights.mostEfficient,
                  Icon:  CheckCircle,
                  color: '#4ade80',
                  delay: '0.07s',
                },
                {
                  label: 'Days Remaining',
                  value: `${insights.daysRemaining} days left`,
                  Icon:  Calendar,
                  color: '#c9a84c',
                  delay: '0.14s',
                },
              ].map(({ label, value, Icon, color, delay }) => (
                <div
                  key={label}
                  className="card-in bg-[#1a1a1a] rounded-xl p-4 border border-white/[0.06] flex items-center gap-3"
                  style={{ animationDelay: delay }}
                >
                  <div
                    className="p-2 rounded-lg shrink-0"
                    style={{ backgroundColor: `${color}18` }}
                  >
                    <Icon size={18} style={{ color }} />
                  </div>
                  <div>
                    <p className="text-white/40 text-[10px] uppercase tracking-[0.12em]">{label}</p>
                    <p className="text-white font-medium text-sm mt-0.5">{value}</p>
                  </div>
                </div>
              ))}
            </div>

            {/* ── Bar Chart: Budget vs Actual ── */}
            <div className="bg-[#1a1a1a] rounded-xl border border-white/[0.06] p-5">
              <h3 className="ds text-xl mb-5">Budget vs Actual</h3>
              <ResponsiveContainer width="100%" height={280}>
                <BarChart
                  data={barData}
                  margin={{ top: 4, right: 4, left: -8, bottom: 0 }}
                  barCategoryGap="30%"
                >
                  <CartesianGrid
                    strokeDasharray="3 3"
                    stroke="rgba(255,255,255,0.04)"
                    vertical={false}
                  />
                  <XAxis
                    dataKey="name"
                    tick={{ fill: 'rgba(255,255,255,0.35)', fontSize: 11 }}
                    axisLine={false}
                    tickLine={false}
                  />
                  <YAxis
                    tick={{ fill: 'rgba(255,255,255,0.35)', fontSize: 11 }}
                    axisLine={false}
                    tickLine={false}
                    tickFormatter={(v) => `${(v / 1000).toFixed(0)}K`}
                  />
                  <Tooltip
                    cursor={{ fill: 'rgba(255,255,255,0.04)' }}
                    contentStyle={{
                      backgroundColor: '#1a1a1a',
                      border: '1px solid rgba(255,255,255,0.1)',
                      borderRadius: '8px',
                      color: 'white',
                      fontSize: '12px',
                    }}
                    formatter={(v) => [fmt(v)]}
                  />
                  <Legend
                    wrapperStyle={{
                      color: 'rgba(255,255,255,0.4)',
                      fontSize: '12px',
                      paddingTop: '8px',
                    }}
                  />
                  {/* Gold bars = budget */}
                  <Bar dataKey="Budget" fill="#c9a84c" radius={[3, 3, 0, 0]} maxBarSize={32} />
                  {/* Green/red bars = actual spend */}
                  <Bar dataKey="Actual" radius={[3, 3, 0, 0]} maxBarSize={32}>
                    {barData.map((entry, index) => (
                      <Cell
                        key={`cell-${index}`}
                        fill={entry.Actual > entry.Budget ? '#f87171' : '#4ade80'}
                      />
                    ))}
                  </Bar>
                </BarChart>
              </ResponsiveContainer>
            </div>

            {/* ── Pie + Trend side by side ── */}
            <div className="grid lg:grid-cols-2 gap-5">

              {/* Donut: Spending Distribution */}
              <div className="bg-[#1a1a1a] rounded-xl border border-white/[0.06] p-5">
                <h3 className="ds text-xl mb-4">Spending Distribution</h3>
                {pieData.length === 0 ? (
                  <div className="h-56 flex items-center justify-center text-white/30 text-sm">
                    No spending data yet.
                  </div>
                ) : (
                  <ResponsiveContainer width="100%" height={230}>
                    <PieChart>
                      <Pie
                        data={pieData}
                        cx="50%"
                        cy="45%"
                        innerRadius={58}
                        outerRadius={92}
                        paddingAngle={3}
                        dataKey="value"
                        strokeWidth={0}
                      >
                        {pieData.map((entry, index) => (
                          <Cell key={`pie-${index}`} fill={entry.color} />
                        ))}
                      </Pie>
                      <Tooltip
                        contentStyle={{
                          backgroundColor: '#1a1a1a',
                          border: '1px solid rgba(255,255,255,0.1)',
                          borderRadius: '8px',
                          color: 'white',
                          fontSize: '12px',
                        }}
                        formatter={(v, name) => [fmt(v), name]}
                      />
                      <Legend
                        wrapperStyle={{ color: 'rgba(255,255,255,0.4)', fontSize: '11px' }}
                        iconType="circle"
                        iconSize={8}
                      />
                    </PieChart>
                  </ResponsiveContainer>
                )}
              </div>

              {/* Area: Cumulative Monthly Trend */}
              <div className="bg-[#1a1a1a] rounded-xl border border-white/[0.06] p-5">
                <h3 className="ds text-xl mb-4">Monthly Spend Trend</h3>
                {trendData.length === 0 ? (
                  <div className="h-56 flex items-center justify-center text-white/30 text-sm">
                    No data yet.
                  </div>
                ) : (
                  <ResponsiveContainer width="100%" height={230}>
                    <AreaChart
                      data={trendData}
                      margin={{ top: 4, right: 4, left: -8, bottom: 0 }}
                    >
                      <defs>
                        <linearGradient id="goldGrad" x1="0" y1="0" x2="0" y2="1">
                          <stop offset="5%"  stopColor="#c9a84c" stopOpacity={0.28} />
                          <stop offset="95%" stopColor="#c9a84c" stopOpacity={0}    />
                        </linearGradient>
                      </defs>
                      <CartesianGrid
                        strokeDasharray="3 3"
                        stroke="rgba(255,255,255,0.04)"
                        vertical={false}
                      />
                      <XAxis
                        dataKey="day"
                        tick={{ fill: 'rgba(255,255,255,0.35)', fontSize: 11 }}
                        axisLine={false}
                        tickLine={false}
                        interval={Math.floor(trendData.length / 5)}
                      />
                      <YAxis
                        tick={{ fill: 'rgba(255,255,255,0.35)', fontSize: 11 }}
                        axisLine={false}
                        tickLine={false}
                        tickFormatter={(v) => `${(v / 1000).toFixed(0)}K`}
                      />
                      <Tooltip
                        contentStyle={{
                          backgroundColor: '#1a1a1a',
                          border: '1px solid rgba(255,255,255,0.1)',
                          borderRadius: '8px',
                          color: 'white',
                          fontSize: '12px',
                        }}
                        formatter={(v) => [fmt(v), 'Cumulative']}
                        labelFormatter={(label) => `Day ${label}`}
                      />
                      <Area
                        type="monotone"
                        dataKey="spent"
                        stroke="#c9a84c"
                        strokeWidth={2}
                        fill="url(#goldGrad)"
                        dot={false}
                        activeDot={{ r: 4, fill: '#c9a84c', stroke: '#0f0f0f', strokeWidth: 2 }}
                      />
                    </AreaChart>
                  </ResponsiveContainer>
                )}
              </div>
            </div>
          </div>
        )}
      </main>

      {/* ── Footer ── */}
      <footer className="max-w-5xl mx-auto px-4 sm:px-6 py-6 mt-4 border-t border-white/[0.04]">
        <p className="jb text-white/15 text-xs text-center">
          FinanceOS · All data is in-memory only — no data is saved between sessions
        </p>
      </footer>
    </div>
  );
}
