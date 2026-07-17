// 日期时间格式处理，把日期统一转化成 YYYY-MM-DD hh:mm:ss 的格式
const dateTime = time => {
  if (!time) return '';

  // 如果 time 是字符串，转成数字
  let timestamp = typeof time === 'string' ? parseInt(time) : time;

  // 检查是否是有效数字
  if (isNaN(timestamp) || timestamp <= 0) return '';

  let newDate = new Date(timestamp);

  // 检查日期是否有效
  if (isNaN(newDate.getTime())) return '';

  let dateCollection = {
    y: newDate.getFullYear(),
    M: newDate.getMonth() + 1,
    d: newDate.getDate(),
    h: newDate.getHours(),
    m: newDate.getMinutes(),
    s: newDate.getSeconds()
  };

  for (let i in dateCollection) {
    if (dateCollection[i] < 10) dateCollection[i] = `0${dateCollection[i]}`;
  }

  let { y, M, d, h, m, s } = dateCollection;
  return `${y}-${M}-${d} ${h}:${m}:${s}`;
}

// 供应商组件 - 显示所有已添加的食品
const Farm = {
  data() {
    return {
      activeTab: 'add',
      foodList: [],
      popoverData: {},
      currPage: 1,
      pageSize: 10,
      total: 0,
      popup: false,
      traceNumber: '',
      foodDetail: '',
      dialogFormVisible: false
    }
  },
  components: {
    CreateVegetable,
    FoodDetail,
    AddVegetable
  },
  computed: {
    renderList() {
      return this.foodList.slice((this.currPage - 1) * this.pageSize, this.currPage * this.pageSize)
    }
  },
  template: `
    <div>
      <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px;">
        <el-button type="primary" @click="popup = true">新建食品</el-button>
        <el-button type="danger" @click="$emit('logout', false)" class="button-logout">退出</el-button>
      </div>

      <el-table
        :data="renderList"
        style="width: 100%"
        :default-sort="{prop: 'produce_time', order: 'descending'}"
      >
        <el-table-column prop="traceNumber" label="溯源码" width="120">
          <template slot-scope="scope">
            <el-popover trigger="click" placement="top" @show="handlePopover(scope.row)" :width="400">
              <FoodDetail :food="popoverData" />
              <div slot="reference">
                <span style="color: blue; cursor: pointer;">{{ scope.row.traceNumber }}</span>
              </div>
            </el-popover>
          </template>
        </el-table-column>
        <el-table-column prop="name" label="食品名称"></el-table-column>
        <el-table-column prop="produce_time" label="创建时间" sortable>
          <template slot-scope="scope">
            {{ dateTime(scope.row.produce_time) }}
          </template>
        </el-table-column>
        <el-table-column prop="from" label="供应商"></el-table-column>
        <el-table-column prop="status" label="当前状态">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.status === 0" type="warning">生产中</el-tag>
            <el-tag v-else-if="scope.row.status === 1" type="success">分销中</el-tag>
            <el-tag v-else-if="scope.row.status === 2" type="info">已出售</el-tag>
            <el-tag v-else type="info">未知</el-tag>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="currPage"
        :page-sizes="[5, 10, 20]"
        :page-size="pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        style="margin-top: 20px;"
      >
      </el-pagination>

      <CreateVegetable :dialogFormVisible="popup" @popup="handlePopup" @confirmPopup="formSubmit" />
      <AddVegetable :dialogFormVisible="false" :user="0" :food="[]" />
    </div>
  `,
  mounted() {
    this.getData();
  },
  methods: {
    getData() {
      axios({
        method: 'get',
        url: '/foodlist'
      })
      .then(ret => {
        let list = [];
        ret.data.forEach(item => {
          try {
            let obj = typeof item === 'string' ? JSON.parse(item) : item;
            list.push({
              traceNumber: obj.traceNumber || '',
              name: obj.name || '',
              from: obj.produce || '',
              to: obj.current || '',
              produce_time: obj.timestamp || '',
              from_address: obj.address || '',
              quality: obj.quality || '',
              to_address: obj.to_address || '',
              status: obj.status,
            });
          } catch(e) {
            console.log('解析失败:', item);
          }
        });
        this.foodList = list.reverse();
        this.total = this.foodList.length;
      })
      .catch(err => {
        console.log(err);
      });
    },

    handlePopup(val) {
      this.popup = val;
    },

    formSubmit(formData) {
      axios({
        method: 'post',
        url: '/produce',
        data: {
          ...formData,
          quality: parseInt(formData.quality),
          traceNumber: parseInt(formData.traceNumber)
        }
      })
      .then(ret => {
        if (ret.data.ret !== 1) {
          this.$message({
            message: ret.data.msg || '创建失败',
            type: 'error',
            center: true
          });
          return;
        }
        this.$message({
          message: '创建成功',
          type: 'success',
          center: true
        });
        this.popup = false;
        this.getData();
      })
      .catch(err => {
        console.log(err);
        this.$message({
          message: '创建失败，请检查网络',
          type: 'error',
          center: true
        });
      });
    },

    handlePopover(data) {
      this.popoverData = data;
    },

    handleSizeChange(val) {
      this.pageSize = val;
    },

    handleCurrentChange(val) {
      this.currPage = val;
    }
  }
}

// 生产/加工商组件 - 只显示分销中的食品
const Agent = {
  mixins: [mixin],
  data() {
    return {
      user: 1,
    }
  }
}

// 零售商组件 - 只显示已出售的食品
const Mall = {
  mixins: [mixin],
  data() {
    return {
      user: 2,
    }
  }
}

// 消费者组件 (彻底重构了退出按钮的宿主结构)
// 消费者组件
// 消费者组件
const Consumer = {
  components: {
    TraceDetail
  },
  data() {
    return {
      form: {
        traceNumber: '',
      },
      foodDetail: '',
      onSearch: false,
    }
  },
  template: `
    <el-row :gutter="20" class="consumer-container" style="height: calc(100vh - 100px); margin: 0;">
      <el-col :span="6" style="height: 100%;">
        <div class="consumer-left-box">
          <el-form :model="form" ref="form">
            <h4>溯源码搜索</h4>
            <el-divider></el-divider>
            <el-form-item label="请输入您想要查询的溯源码" prop="traceNumber">
              <el-input v-model.number="form.traceNumber" type="textarea" :rows="3" autocomplete="off" @clear="onSearch = false;"></el-input>
            </el-form-item>
            <el-button type="primary" @click="onSubmit" class="search-btn">查询</el-button>
          </el-form>
        </div>
      </el-col>

      <el-col :span="18" style="height: 100%;">
        <div class="consumer-right-box">
          <div class="consumer-logout-wrap">
            <el-button type="danger" size="small" @click="$emit('logout', false)" class="button-logout">退出登录</el-button>
          </div>

          <div class="consumer-scroll-content">
            <div v-if="!onSearch" class="consumer-tip">请在左侧查询栏中输入溯源码进行查询</div>
            <div v-if="onSearch && foodDetail.length === 0" class="consumer-tip">该溯源码无对应信息，请确认后重新查询</div>

            <div v-if="onSearch && foodDetail.length" class="trace-wrapper">
              <TraceDetail :food-detail="foodDetail" :user="3" />
            </div>
          </div>
        </div>
      </el-col>
    </el-row>
  `,
  methods: {
    onSubmit() {
      if (!this.form.traceNumber) {
        this.$message.error('请输入溯源码');
        return;
      }
      this.onSearch = true;
      axios({
        method: 'get',
        url: `/trace?traceNumber=${this.form.traceNumber}`
      })
        .then(ret => {
          this.foodDetail = ret.data || []
        })
        .catch(err => {
          console.log(err);
        })
    }
  }
}