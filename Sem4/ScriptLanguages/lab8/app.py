import tkinter as tk
from tkinter import ttk, filedialog, messagebox
from datetime import datetime
import sys
import os

sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

from lista8.log_parser import (
    filter_by_success_only,
    read_log_file,
    filter_by_date_range,
    get_entry_display_text,
    get_status_color,
)
from tkcalendar import DateEntry


class LogBrowserApp:
    def __init__(self, root):
        self.root = root
        self.root.title("Log browser")
        self.root.minsize(860, 540)

        self.all_entries = []
        self.filtered_entries = []
        self.current_index = -1

        self._build_ui()

    def _build_ui(self):
        self.root.columnconfigure(0, weight=1)
        self.root.rowconfigure(2, weight=1)

        top = ttk.Frame(self.root, padding=(8, 8, 8, 4))
        top.grid(row=0, column=0, sticky="ew")
        top.columnconfigure(0, weight=1)

        self.path_var = tk.StringVar()
        ttk.Entry(top, textvariable=self.path_var).grid(row=0, column=0, sticky="ew", padx=(0, 6))
        ttk.Button(top, text="Open", command=self._open_file).grid(row=0, column=1)

        fbar = ttk.Frame(self.root, padding=(8, 2, 8, 4))
        fbar.grid(row=1, column=0, sticky="ew")

        ttk.Label(fbar, text="From").pack(side=tk.LEFT)
        self.from_var = tk.StringVar()
        DateEntry(fbar, textvariable=self.from_var, date_pattern="yyyy-mm-dd", width=12).pack(
            side=tk.LEFT, padx=(3, 10)
        )

        ttk.Label(fbar, text="To").pack(side=tk.LEFT)
        self.to_var = tk.StringVar()
        DateEntry(fbar, textvariable=self.to_var, date_pattern="yyyy-mm-dd", width=12).pack(
            side=tk.LEFT, padx=(3, 10)
        )

        self.show_success_only_var = tk.BooleanVar()
        ttk.Checkbutton(fbar, text="Show success only", variable=self.show_success_only_var).pack(side=tk.LEFT, padx=(0, 10))

        ttk.Button(fbar, text="Filter", command=self._apply_filter).pack(side=tk.LEFT, padx=(0, 4))
        ttk.Button(fbar, text="Reset", command=self._reset_filter).pack(side=tk.LEFT)


        content = ttk.Frame(self.root, padding=(8, 4, 8, 4))
        content.grid(row=2, column=0, sticky="nsew")
        content.rowconfigure(0, weight=1)
        content.columnconfigure(0, weight=1)

        master_frame = ttk.Frame(content)
        master_frame.grid(row=0, column=0, sticky="nsew")
        master_frame.rowconfigure(0, weight=1)
        master_frame.columnconfigure(0, weight=1)

        scrollbar = ttk.Scrollbar(master_frame, orient=tk.VERTICAL)
        self.listbox = tk.Listbox(
            master_frame,
            yscrollcommand=scrollbar.set,
            selectmode=tk.SINGLE,
            activestyle="dotbox",
            font=("Courier", 9),
        )
        scrollbar.config(command=self.listbox.yview)
        self.listbox.grid(row=0, column=0, sticky="nsew")
        scrollbar.grid(row=0, column=1, sticky="ns")
        self.listbox.bind("<<ListboxSelect>>", self._on_select)

        ttk.Separator(content, orient=tk.VERTICAL).grid(row=0, column=1, sticky="ns", padx=8)

        detail = ttk.Frame(content, padding=(0, 4))
        detail.grid(row=0, column=2, sticky="n")
        self._build_detail(detail)

        nav = ttk.Frame(self.root, padding=(8, 4, 8, 8))
        nav.grid(row=3, column=0, sticky="ew")

        self.prev_btn = ttk.Button(nav, text="Previous", command=self._prev, state=tk.DISABLED)
        self.prev_btn.pack(side=tk.LEFT)
        self.next_btn = ttk.Button(nav, text="Next", command=self._next, state=tk.DISABLED)
        self.next_btn.pack(side=tk.RIGHT)

    def _build_detail(self, parent):
        def row(r, label, var, width=22, colspan=3):
            ttk.Label(parent, text=label).grid(row=r, column=0, sticky="w", pady=4, padx=(0, 10))
            ttk.Entry(parent, textvariable=var, state="readonly", width=width).grid(
                row=r, column=1, columnspan=colspan, sticky="ew", pady=4
            )

        self.remote_host_var = tk.StringVar()
        self.date_var = tk.StringVar()
        self.time_var = tk.StringVar()
        self.tz_var = tk.StringVar(value="UTC")
        self.method_var = tk.StringVar()
        self.resource_var = tk.StringVar()
        self.size_var = tk.StringVar()

        row(0, "Remote host", self.remote_host_var)
        row(1, "Date", self.date_var)

        ttk.Label(parent, text="Time").grid(row=2, column=0, sticky="w", pady=4, padx=(0, 10))
        ttk.Entry(parent, textvariable=self.time_var, state="readonly", width=10).grid(
            row=2, column=1, sticky="w", pady=4
        )
        ttk.Label(parent, text="Timezone:").grid(row=2, column=2, sticky="w", padx=(8, 4), pady=4)
        ttk.Entry(parent, textvariable=self.tz_var, state="readonly", width=10).grid(
            row=2, column=3, sticky="w", pady=4
        )

        ttk.Label(parent, text="Status code:").grid(row=3, column=0, sticky="w", pady=4, padx=(0, 10))
        sc_frame = ttk.Frame(parent)
        sc_frame.grid(row=3, column=1, sticky="w", pady=4)
        self.status_canvas = tk.Canvas(sc_frame, width=48, height=30, highlightthickness=0)
        self.status_canvas.pack(side=tk.LEFT)
        self._oval = self.status_canvas.create_oval(3, 3, 45, 27, fill="#aaaaaa", outline="")
        self._oval_text = self.status_canvas.create_text(
            24, 15, text="", fill="white", font=("Arial", 9, "bold")
        )

        ttk.Label(parent, text="Method:").grid(row=3, column=2, sticky="w", padx=(8, 4), pady=4)
        ttk.Entry(parent, textvariable=self.method_var, state="readonly", width=10).grid(
            row=3, column=3, sticky="w", pady=4
        )

        ttk.Label(parent, text="Resource:").grid(row=4, column=0, sticky="w", pady=4, padx=(0, 10))
        ttk.Entry(parent, textvariable=self.resource_var, state="readonly", width=32).grid(
            row=4, column=1, columnspan=3, sticky="ew", pady=4
        )

        ttk.Label(parent, text="Size").grid(row=5, column=0, sticky="w", pady=4, padx=(0, 10))
        ttk.Label(parent, textvariable=self.size_var).grid(
            row=5, column=1, columnspan=3, sticky="w", pady=4
        )

        parent.columnconfigure(1, weight=1)

    # --- file loading ---

    def _open_file(self):
        path = self.path_var.get().strip()
        if not path:
            path = filedialog.askopenfilename(
                title="Open log file",
                filetypes=[("Log files", "*.log"), ("All files", "*.*")],
            )
            if not path:
                return
            self.path_var.set(path)
        else:
            if not os.path.isfile(path):
                messagebox.showerror("Error", f"File not found:\n{path}")
                return

        try:
            self.all_entries = read_log_file(path)
        except Exception as exc:
            messagebox.showerror("Error", f"Failed to read file:\n{exc}")
            return

        if not self.all_entries:
            messagebox.showwarning("Warning", "No valid log entries found.")
            return

        self.filtered_entries = list(self.all_entries)
        self._set_default_dates()
        self._populate_list()

    def _set_default_dates(self):
        dates = [e[0].date() for e in self.all_entries]
        self.from_var.set(min(dates).strftime("%Y-%m-%d"))
        self.to_var.set(max(dates).strftime("%Y-%m-%d"))

    # --- filtering ---

    def _apply_filter(self):
        try:
            from_date = datetime.strptime(self.from_var.get().strip(), "%Y-%m-%d").date()
            to_date = datetime.strptime(self.to_var.get().strip(), "%Y-%m-%d").date()
        except ValueError:
            messagebox.showerror("Error", "Invalid date format. Use YYYY-MM-DD.")
            return
        if from_date > to_date:
            messagebox.showerror("Error", '"From" date must not be after "To" date.')
            return
        self.filtered_entries = filter_by_date_range(self.all_entries, from_date, to_date)
        if self.show_success_only_var.get():
            self.filtered_entries = filter_by_success_only(self.filtered_entries)
        self._populate_list()

    def _reset_filter(self):
        self.filtered_entries = list(self.all_entries)
        self._set_default_dates()
        self._populate_list()
        self.show_success_only_var.set(False)

    # --- list ---

    def _populate_list(self):
        self.listbox.delete(0, tk.END)
        for entry in self.filtered_entries:
            self.listbox.insert(tk.END, get_entry_display_text(entry))
        self._clear_detail()
        self.current_index = -1
        self._update_buttons()

    def _on_select(self, _event):
        sel = self.listbox.curselection()
        if sel:
            self.current_index = sel[0]
            self._show_detail(self.current_index)
            self._update_buttons()

    # --- detail ---

    def _show_detail(self, idx):
        if not (0 <= idx < len(self.filtered_entries)):
            return
        entry = self.filtered_entries[idx]
        ts, uid, ip, ip_p, resp_h, resp_p, method, host, uri, status_code = entry[:10]
        resp_size = entry[11] if len(entry) > 11 else None

        self.remote_host_var.set(ip)
        self.date_var.set(ts.strftime("%Y-%m-%d"))
        self.time_var.set(ts.strftime("%H:%M:%S"))
        self.method_var.set(method or "-")
        self.resource_var.set(uri)
        self.size_var.set(f"{resp_size} Bytes" if resp_size is not None else "-")

        color = get_status_color(status_code)
        code_str = str(status_code) if status_code is not None else "?"
        self.status_canvas.itemconfig(self._oval, fill=color)
        self.status_canvas.itemconfig(self._oval_text, text=code_str)

    def _clear_detail(self):
        for var in (self.remote_host_var, self.date_var, self.time_var, self.method_var, self.resource_var):
            var.set("")
        self.size_var.set("")
        self.status_canvas.itemconfig(self._oval, fill="#aaaaaa")
        self.status_canvas.itemconfig(self._oval_text, text="")

    # --- navigation ---

    def _prev(self):
        if self.current_index > 0:
            self.current_index -= 1
            self._select_and_show(self.current_index)

    def _next(self):
        if self.current_index < len(self.filtered_entries) - 1:
            self.current_index += 1
            self._select_and_show(self.current_index)

    def _select_and_show(self, idx):
        self.listbox.selection_clear(0, tk.END)
        self.listbox.selection_set(idx)
        self.listbox.see(idx)
        self._show_detail(idx)
        self._update_buttons()

    def _update_buttons(self):
        n = len(self.filtered_entries)
        idx = self.current_index
        self.prev_btn.config(state=tk.NORMAL if idx > 0 else tk.DISABLED)
        self.next_btn.config(state=tk.NORMAL if 0 <= idx < n - 1 else tk.DISABLED)


def main():
    root = tk.Tk()
    LogBrowserApp(root)
    root.mainloop()


if __name__ == "__main__":
    main()
